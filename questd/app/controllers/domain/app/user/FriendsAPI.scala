package controllers.domain.app.user

import components._
import controllers.domain.{DomainAPIComponent, _}
import controllers.domain.app.protocol.ProfileModificationResult._
import controllers.domain.helpers._
import controllers.services.socialnetworks.client.{User => SNUser}
import models.domain.common.Assets
import models.domain.user._
import models.domain.user.friends.{ReferralStatus, FriendshipStatus, Friendship}
import models.domain.user.message.{MessageFriendshipAccepted, MessageFriendshipRemoved}
import models.domain.user.profile.{TaskType, Profile}
import play.Logger

case class GetFriendsRequest(
  user: User)
case class GetFriendsResult(
  allowed: ProfileModificationResult,
  users: List[Friendship])

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

case class CreateFriendshipRequest(
  user: User,
  friendId: String,
  isReferredBy: Boolean = false,
  referredWithContentId: Option[String] = None)
case class CreateFriendshipResult(
  user: User)

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
    } map { r =>

      OkApiResult(GetFriendsResult(
        allowed = OK,
        users = r.user.friends))

    }

  }

  /**
   * How much it'll take invite person to become a friend.
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
        case Some(potentialFriend) =>
          request.user.canAddFriend(potentialFriend) match {
            case OK =>

              val cost = request.user.costToAddFriend(potentialFriend)
              adjustAssets(AdjustAssetsRequest(user = request.user, change = -cost)) map { r =>

                db.user.askFriendship(
                  r.user.id,
                  request.friendId,
                  Friendship(request.friendId, FriendshipStatus.Invited),
                  Friendship(r.user.id, FriendshipStatus.Invites))

                // remove this soplia whn check in logic will be implemented.
                if (potentialFriend.banned.contains(r.user.id)) {
                  respondFriendship(RespondFriendshipRequest(potentialFriend, r.user.id, accept = false))
                }

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
      !request.user.friends.exists {
        x => (x.friendId == request.friendId) && (x.status == FriendshipStatus.Invites)
      }) {
      OkApiResult(RespondFriendshipResult(allowed = OutOfContent))
    } else {
      if (request.accept) {
        db.user.readById(request.friendId) ifSome { friend =>
          request.user.canAcceptFriendship(friend) match {
            case OK =>
              db.user.updateFriendship(
                request.user.id,
                request.friendId,
                FriendshipStatus.Accepted.toString,
                FriendshipStatus.Accepted.toString)

              // Sending message about good response on friendship.
              sendMessage(SendMessageRequest(friend, MessageFriendshipAccepted(request.user.id)))

              // Removing each other from following.
              db.user.removeFromFollowing(request.user.id, request.friendId)
              db.user.removeFromFollowing(request.friendId, request.user.id)

              OkApiResult(RespondFriendshipResult(OK))
            case a =>
              OkApiResult(RespondFriendshipResult(a))
          }
        }
      } else {
        db.user.removeFriendship(request.user.id, request.friendId)

        // sending message for rejected response.
        db.user.readById(request.friendId) ifSome { f =>
          sendMessage(SendMessageRequest(f, message.MessageFriendshipRejected(request.user.id)))
        }

        OkApiResult(RespondFriendshipResult(OK))
      }
    }
  }

  /**
   * Respond from a person to friendship request
   */
  def removeFromFriends(request: RemoveFromFriendsRequest): ApiResult[RemoveFromFriendsResult] = handleDbException {
    if (request.friendId == request.user.id
      || !request.user.friends.exists {
      x => (x.friendId == request.friendId) && (x.status == FriendshipStatus.Accepted || x.status == FriendshipStatus
        .Invited)
    }) {
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
   * Creates already accepted friendship between two people.
   */
  def createFriendship(request: CreateFriendshipRequest): ApiResult[CreateFriendshipResult] = handleDbException {
    import request._

    db.user.readById(friendId).fold() { referrer => // TODO: null pointer exception here in one of tests.
      db.user.addFriendship(
        referrer.id,
        Friendship(
          user.id,
          FriendshipStatus.Accepted,
          referralStatus = if (request.isReferredBy) ReferralStatus.Refers else ReferralStatus.None
        ))

      db.user.addFriendship(
        user.id,
        Friendship(
          referrer.id,
          FriendshipStatus.Accepted,
          referralStatus = if (request.isReferredBy) ReferralStatus.ReferredBy else ReferralStatus.None,
          referredWithContentId = referredWithContentId
        ))
    }

    OkApiResult(CreateFriendshipResult(user))
  }

  /**
   * Create friendships for invitation requests for given SN user.
   * @param request Request what parametrizes our the API
   * @return updated user.
   */
  def processFriendshipInvitationsFromSN(request: ProcessFriendshipInvitationsFromSNRequest): ApiResult[ProcessFriendshipInvitationsFromSNResult] = handleDbException {
    // All exceptions are wrapped and returned as Internal error which is not good.
    // Only real internal errors should be reported as internal errors.
    Logger.trace(s"processFriendshipInvitationsFromSN of count ${request.snUser.invitations.length} for ${request.user.id} aka ${request.user.profile.publicProfile.bio.name}")

    val rv = request.snUser.invitations.foldLeft(request.user) { (u, i) =>
      Logger.trace(s"Invitation from ${i.inviterSnId} in ${i.snName}")

      // Read by snid returns nothing.
      db.user.readBySNid(i.snName, i.inviterSnId).fold {
        Logger.trace(s"Unable to find ${i.inviterSnId} in ${i.snName} in db")
      } { friend =>
        Logger.trace(s"becoming friends with ${friend.profile.publicProfile.bio.name}")

        def becomeFriend(me: User, newFriend: User, status: FriendshipStatus.Value, referralStatus: Option[ReferralStatus.Value] = None): Unit = {
          if (me.friends.map(_.friendId).contains(newFriend.id)) {
            Logger.trace(s"updating friendship with referral status $referralStatus")
            db.user.updateFriendship(me.id, newFriend.id, Some(status.toString), referralStatus.map(_.toString))
          } else {
            Logger.trace(s"creating friendship with referral status $referralStatus")
            db.user.addFriendship(me.id, Friendship(newFriend.id, status, referralStatus.getOrElse(ReferralStatus.None)))
          }
        }

        // Auto accept first social network invitation for newcomers.
        if (!request.user.friends.exists(_.status == FriendshipStatus.Accepted)) {
          becomeFriend(request.user, friend, FriendshipStatus.Accepted, Some(ReferralStatus.ReferredBy))
          becomeFriend(friend, request.user, FriendshipStatus.Accepted, Some(ReferralStatus.Refers))
          sendMessage(SendMessageRequest(
            friend, MessageFriendshipAccepted(request.user.id)))
        } else {
          becomeFriend(request.user, friend, FriendshipStatus.Invites)
          becomeFriend(friend, request.user, FriendshipStatus.Invited)
        }
      }

      Logger.trace(s"deleting invitation from ${i.inviterSnId} in ${i.snName}")
      i.delete()
      u
    }

    OkApiResult(ProcessFriendshipInvitationsFromSNResult(rv))
  }
}

