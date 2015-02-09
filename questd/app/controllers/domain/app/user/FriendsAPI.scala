package controllers.domain.app.user

import controllers.sn.client.{User => SNUser}
import models.domain._
import controllers.domain.DomainAPIComponent
import components._
import controllers.domain._
import controllers.domain.helpers._
import controllers.domain.app.protocol.ProfileModificationResult._
import play.Logger

case class GetFriendsRequest(
  user: User)
case class GetFriendsResult(
  allowed: ProfileModificationResult,
  userIds: List[Friendship])

case class CostToRequestFriendshipRequest(
  user: User,
  friendId: String)
case class CostToRequestFriendshipResult(
  allowed: ProfileModificationResult,
  cost: Option[Assets] = None)

case class AskFriendshipRequest(
  user: User,
  friendId: String)
case class AskFriendshipResult(
  allowed: ProfileModificationResult,
  profile: Option[Profile] = None)

case class RespondFriendshipRequest(
  user: User,
  friendId: String,
  accept: Boolean)
case class RespondFriendshipResult(
  allowed: ProfileModificationResult)

case class RemoveFromFriendsRequest(
  user: User,
  friendId: String)
case class RemoveFromFriendsResult(
  allowed: ProfileModificationResult)

case class ProcessFriendshipInvitationsFromSNRequest(
  user: User,
  snUser: SNUser)
case class ProcessFriendshipInvitationsFromSNResult(user: User)

private[domain] trait FriendsAPI { this: DBAccessor with DomainAPIComponent#DomainAPI =>

  /**
   * Get ids of users from our friends list.
   */
  def getFriends(request: GetFriendsRequest): ApiResult[GetFriendsResult] = handleDbException {

    {
      makeTask(MakeTaskRequest(request.user, taskType = Some(TaskType.LookThroughFriendshipProposals)))
    } ifOk { r =>

      OkApiResult(GetFriendsResult(
        allowed = OK,
        userIds = r.user.friends))

    }

  }

  /**
   * How much it'll take invite persone to become a friend.
   */
  def costToRequestFriendship(request: CostToRequestFriendshipRequest): ApiResult[CostToRequestFriendshipResult] = handleDbException {

    db.user.readById(request.friendId) match {
      case Some(u) =>
        OkApiResult(CostToRequestFriendshipResult(
          allowed = OK,
          cost = Some(request.user.costToAddFriend(u))))

      case None =>
        OkApiResult(CostToRequestFriendshipResult(
          allowed = OutOfContent))
    }
  }

  /**
   * Ask person to become our friend.
   */
  def askFriendship(request: AskFriendshipRequest): ApiResult[AskFriendshipResult] = handleDbException {

    if (request.friendId == request.user.id ||
      request.user.friends.map(_.friendId).contains(request.friendId)) {
      OkApiResult(AskFriendshipResult(
        allowed = OutOfContent))
    } else {
      db.user.readById(request.friendId) match {
        case Some(u) =>
          request.user.canAddFriend(u) match {
            case OK =>

              val cost = request.user.costToAddFriend(u)
              adjustAssets(AdjustAssetsRequest(user = request.user, cost = Some(cost))) ifOk { r =>

                db.user.askFriendship(
                  r.user.id,
                  request.friendId,
                  Friendship(request.friendId, FriendshipStatus.Invited),
                  Friendship(r.user.id, FriendshipStatus.Invites))

                OkApiResult(AskFriendshipResult(OK, Some(r.user.profile)))
              }
            case a => OkApiResult(AskFriendshipResult(a))
          }

        case None =>
          OkApiResult(AskFriendshipResult(
            allowed = OutOfContent))
      }
    }

  }

  /**
   * Respond from a person to friendship request
   */
  def respondFriendship(request: RespondFriendshipRequest): ApiResult[RespondFriendshipResult] = handleDbException {

    if (request.friendId == request.user.id ||
      request.user.friends.find {
        x => (x.friendId == request.friendId) && (x.status == FriendshipStatus.Invites)
      } == None) {
      OkApiResult(RespondFriendshipResult(allowed = OutOfContent))
    } else {
      if (request.accept) {
        db.user.updateFriendship(
          request.user.id,
          request.friendId,
          FriendshipStatus.Accepted.toString,
          FriendshipStatus.Accepted.toString)

        // Sending message about good response on friendship.
        db.user.readById(request.friendId) ifSome { f =>
          sendMessage(SendMessageRequest(f, MessageFriendshipAccepted(request.user.id)))
        }

      } else {

        db.user.removeFriendship(request.user.id, request.friendId)

        // sending message for rejected response.
        db.user.readById(request.friendId) ifSome { f =>
          sendMessage(SendMessageRequest(f, MessageFriendshipRejected(request.user.id)))
        }
      }

      OkApiResult(RespondFriendshipResult(OK))
    }
  }

  /**
   * Respond from a person to friendship request
   */
  def removeFromFriends(request: RemoveFromFriendsRequest): ApiResult[RemoveFromFriendsResult] = handleDbException {
    if (request.friendId == request.user.id
      || request.user.friends.find {
        x => (x.friendId == request.friendId) && (x.status == FriendshipStatus.Accepted || x.status == FriendshipStatus.Invited)
      } == None) {
      OkApiResult(RemoveFromFriendsResult(
        allowed = OutOfContent))
    } else {

      db.user.removeFriendship(request.user.id, request.friendId)

      request.user.friends.find(_.friendId == request.friendId) ifSome { v =>
        // Sending message about removed friend to friend.
        if (v.status == FriendshipStatus.Accepted) {
          db.user.readById(request.friendId) ifSome { f =>
            sendMessage(SendMessageRequest(f, MessageFriendshipRemoved(request.user.id)))
          }
        }

        OkApiResult(RemoveFromFriendsResult(OK))
      }
    }
  }


  /**
   * Create friendships for invitation requests for given SN user.
   * @param request Request what parametrizes our the API
   * @return updated user.
   */
  def processFriendshipInvitationsFromSN(request: ProcessFriendshipInvitationsFromSNRequest): ApiResult[ProcessFriendshipInvitationsFromSNResult] = handleDbException {
    // All exceptions are wrapped and returned as Internal error which is not clean for now but ok since we ignore errors for this call anyways.

    val rv = request.snUser.invitations.foldLeft(request.user) { (u, i) =>
      Logger.trace(s"Invitation from ${i.inviterSnId}")

      db.user.readBySNid(i.snName, i.inviterSnId) foreach { friend =>
        Logger.trace(s"becoming friends with ${friend.profile.publicProfile.bio.name}")

        def becomeFriend(me: User, newfriend: User, status: FriendshipStatus.Value): Unit = {
          if (me.friends.map(_.friendId).contains(newfriend.id)) {
            Logger.trace(s"updating friendship")
            db.user.updateFriendship(me.id, newfriend.id, status.toString)
          } else {
            Logger.trace(s"creating friendship")
            db.user.addFriendship(me.id, Friendship(newfriend.id, status))
          }
        }

        // Autoaccept for newcomers.
        if (request.user.profile.publicProfile.level <= 1) {
          becomeFriend(request.user, friend, FriendshipStatus.Accepted)
          becomeFriend(friend, request.user, FriendshipStatus.Accepted)
          sendMessage(SendMessageRequest(
            friend, MessageFriendshipAccepted(request.user.id)))
        } else {
          becomeFriend(request.user, friend, FriendshipStatus.Invites)
          becomeFriend(friend, request.user, FriendshipStatus.Invited)
        }

      }

      i.delete()
      u
    }

    OkApiResult(ProcessFriendshipInvitationsFromSNResult(rv))
  }
}

