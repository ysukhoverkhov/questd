package controllers.domain.app.user

import components._
import controllers.domain._
import controllers.domain.app.protocol.CommonCode
import controllers.domain.helpers._
import models.domain.user._
import models.domain.user.friends.FriendshipStatus
import models.domain.user.timeline.TimeLineReason


object BanUserCode extends Enumeration with CommonCode {
  val MaxBansCountReached = Value
  val UserNotFound = Value
}
case class BanUserRequest(
  user: User,
  userId: String)
case class BanUserResult(
  allowed: BanUserCode.Value)


object UnbanUserCode extends Enumeration with CommonCode {
  val UserNotBanned = Value
}
case class UnbanUserRequest(
  user: User,
  userId: String)
case class UnbanUserResult(
  allowed: UnbanUserCode.Value)


case class GetBannedUsersRequest(
  user: User,
  pageNumber: Int,
  pageSize: Int)
case class GetBannedUsersResult(
  userIds: List[String],
  pageSize: Int)


private[domain] trait BanAPI { this: DBAccessor with DomainAPIComponent#DomainAPI =>

  /**
   * Bans user and removes his content for us.
   */
  def banUser(request: BanUserRequest): ApiResult[BanUserResult] = handleDbException {
    import BanUserCode._
    import request._

    val maxBannedSize = api.config(api.DefaultConfigParams.BannedUsersMaxLength).toInt

    if (user.banned.length >= maxBannedSize) {
      OkApiResult(BanUserResult(MaxBansCountReached))
    } else {
      // make task to optimize existence calls.
      db.user.readById(userId).fold[ApiResult[BanUserResult]] {
        OkApiResult(BanUserResult(UserNotFound))
      } { userToBan =>

        user.timeLine.filter(_.actorId == userId).foreach( e =>
          db.user.updateTimeLineEntry(user.id, e.id, TimeLineReason.Hidden)
        )

        if (user.following.contains(userId)) {
          removeFromFollowing(RemoveFromFollowingRequest(user, userId))
        }

        user.friends.find(_.friendId == userId).fold() { friendship =>
          if (friendship.status == FriendshipStatus.Invites) {
            respondFriendship(RespondFriendshipRequest(user = user, friendId = userId, accept = false))
          } else {
            removeFromFriends(RemoveFromFriendsRequest(user = user, friendId = userId))
          }
        }

        db.user.addBannedUser(user.id, userId) ifSome { u: User =>
          OkApiResult(BanUserResult(OK))
        }
      }
    }
  }

  /**
   * Unban user we've banned.
   */
  def unbanUser(request: UnbanUserRequest): ApiResult[UnbanUserResult] = handleDbException {
    import UnbanUserCode._
    import request._

    if (user.banned.contains(userId)) {
      db.user.removeBannedUser(user.id, userId) ifSome { u =>
        OkApiResult(UnbanUserResult(OK))
      }
    } else {
      OkApiResult(UnbanUserResult(UserNotBanned))
    }
  }

  /**
   * Ask person to become our friend.
   */
  def getBannedUsers(request: GetBannedUsersRequest): ApiResult[GetBannedUsersResult] = handleDbException {
    val pageSize = adjustedPageSize(request.pageSize)
    val pageNumber = adjustedPageNumber(request.pageNumber)

    OkApiResult(GetBannedUsersResult(
      userIds = request.user.banned
        .slice(pageSize * pageNumber, pageSize * pageNumber + pageSize)
        .take(pageSize),
      pageSize = pageSize))
  }
}

