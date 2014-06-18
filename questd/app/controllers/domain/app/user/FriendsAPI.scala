package controllers.domain.app.user

import models.domain._
import models.store._
import controllers.domain.DomainAPIComponent
import components._
import controllers.domain._
import controllers.domain.helpers._
import logic._
import play.Logger
import controllers.domain.app.protocol.ProfileModificationResult._
import play.api.i18n.Messages

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
  assets: Option[Assets] = None)

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

private[domain] trait FriendsAPI { this: DBAccessor with DomainAPIComponent#DomainAPI =>

  /**
   * Get ids of users from our friends list.
   */
  def getFriends(request: GetFriendsRequest): ApiResult[GetFriendsResult] = handleDbException {

    {

      makeTask(MakeTaskRequest(request.user, TaskType.LookThroughFriendshipProposals))

    } ifOk { r =>

      OkApiResult(Some(GetFriendsResult(
        allowed = OK,
        userIds = r.user.friends)))

    }

  }

  /**
   * How much it'll take invite persone to become a friend.
   */
  def costToRequestFriendship(request: CostToRequestFriendshipRequest): ApiResult[CostToRequestFriendshipResult] = handleDbException {

    db.user.readById(request.friendId) match {
      case Some(u) => {
        OkApiResult(Some(CostToRequestFriendshipResult(
          allowed = OK,
          cost = Some(request.user.costToAddFriend(u)))))
      }

      case None => {
        OkApiResult(Some(CostToRequestFriendshipResult(
          allowed = OutOfContent)))
      }
    }
  }

  /**
   * Ask person to become our friend.
   */
  def askFriendship(request: AskFriendshipRequest): ApiResult[AskFriendshipResult] = handleDbException {

    if (request.friendId == request.user.id ||
      request.user.friends.map(_.friendId).contains(request.friendId)) {
      OkApiResult(Some(AskFriendshipResult(
        allowed = OutOfContent)))
    } else {
      db.user.readById(request.friendId) match {
        case Some(u) => {
          request.user.canAddFriend(u) match {
            case OK => {

              val cost = request.user.costToAddFriend(u)
              adjustAssets(AdjustAssetsRequest(user = request.user, cost = Some(cost))) ifOk { r =>

                db.user.askFriendship(
                  r.user.id,
                  request.friendId,
                  Friendship(request.friendId, FriendshipStatus.Invited),
                  Friendship(r.user.id, FriendshipStatus.Invites))

                OkApiResult(Some(AskFriendshipResult(OK, Some(r.user.profile.assets))))
              }
            }
            case a => OkApiResult(Some(AskFriendshipResult(a)))
          }
        }

        case None => {
          OkApiResult(Some(AskFriendshipResult(
            allowed = OutOfContent)))
        }
      }
    }

  }

  /**
   * Respond from a person to friendship request
   */
  def respondFriendship(request: RespondFriendshipRequest): ApiResult[RespondFriendshipResult] = handleDbException {

    if (request.friendId == request.user.id ||
      request.user.friends.find {
        x => (x.friendId == request.friendId) && (x.status == FriendshipStatus.Invites.toString())
      } == None) {
      OkApiResult(Some(RespondFriendshipResult(allowed = OutOfContent)))
    } else {
      if (request.accept == true) {
        db.user.updateFriendship(
          request.user.id,
          request.friendId,
          FriendshipStatus.Accepted.toString,
          FriendshipStatus.Accepted.toString)

        // Sending message about good response on friendship.
        db.user.readById(request.friendId) match {
          case Some(f) => sendMessage(SendMessageRequest(f, Message(text = Messages("friends.accepted", request.user.id))))
          case None => Logger.error("Unable to find friend for sending him a message " + request.friendId)
        }

      } else {

        db.user.removeFriendship(request.user.id, request.friendId)

        // sending message for rejected response.
        db.user.readById(request.friendId) match {
          case Some(f) => sendMessage(SendMessageRequest(f, Message(text = Messages("friends.rejected", request.user.id))))
          case None => Logger.error("Unable to find friend for sending him a message " + request.friendId)
        }
      }

      OkApiResult(Some(RespondFriendshipResult(OK)))
    }
  }

  /**
   * Respond from a person to friendship request
   */
  def removeFromFriends(request: RemoveFromFriendsRequest): ApiResult[RemoveFromFriendsResult] = handleDbException {
    if (request.friendId == request.user.id
      || request.user.friends.find {
        x => (x.friendId == request.friendId) && (x.status == FriendshipStatus.Accepted.toString() || x.status == FriendshipStatus.Invited.toString())
      } == None) {
      OkApiResult(Some(RemoveFromFriendsResult(
        allowed = OutOfContent)))
    } else {

      db.user.removeFriendship(request.user.id, request.friendId)

      request.user.friends.find(_.friendId == request.friendId) ifSome { v =>
        // Sending message about removed friend to friend.
        if (v.status == FriendshipStatus.Accepted.toString()) {
          db.user.readById(request.friendId) match {
            case Some(f) => sendMessage(SendMessageRequest(f, Message(text = Messages("friends.removed", request.user.id))))
            case None => Logger.error("Unable to find friend for sending him a message " + request.friendId)
          }
        }

        OkApiResult(Some(RemoveFromFriendsResult(OK)))
      }
    }
  }
}

