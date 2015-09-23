package controllers.domain.app.user

import components._
import controllers.domain.app.protocol.ProfileModificationResult._
import controllers.domain.helpers._
import controllers.domain._
import models.domain.user._
import models.domain.user.friends.FriendshipStatus


case class BanUserRequest(
  user: User,
  userId: String)
case class BanUserResult(
  allowed: ProfileModificationResult)

case class UnbanUserRequest(
  user: User,
  userId: String)
case class UnbanUserResult(
  allowed: ProfileModificationResult)

case class GetBannedUsersRequest(
  user: User,
  pageNumber: Int,
  pageSize: Int)
case class GetBannedUsersResult(
  userIds: List[String],
  pageSize: Int,
  hasMore: Boolean)

private[domain] trait BanAPI { this: DBAccessor with DomainAPIComponent#DomainAPI =>

  /**
   * Bans user and removes his content for us.
   */
  def banUser(request: BanUserRequest): ApiResult[BanUserResult] = handleDbException {
    import request._

    // TODO: make task to optimize existence calls.
    db.user.readById(userId).fold[ApiResult[BanUserResult]] {
      OkApiResult(BanUserResult(OutOfContent))
    } { userToBan =>
      // TODO: hide all timeline entries with this user.

      // TODO: test remove user from following.
      if (user.following.contains(userId)) {
        removeFromFollowing(RemoveFromFollowingRequest(user, userId))
      }

      // TODO: test reject friendship.
      user.friends.find(_.friendId == userId).fold() { friendship =>

        if (friendship.status == FriendshipStatus.Invites) {
          respondFriendship(RespondFriendshipRequest(user = user, friendId = userId, accept = false))
        } else {
          removeFromFriends(RemoveFromFriendsRequest(user = user, friendId = userId))
        }
      }

      // TODO: test db is called.
      db.user.addBannedUser(user.id, userId) ifSome { u: User =>
        OkApiResult(BanUserResult(OK))
      }
    }
  }

  /**
   * Unban user we've banned.
   */
  def unbanUser(request: UnbanUserRequest): ApiResult[UnbanUserResult] = handleDbException {
    import request._

    db.user.removeBannedUser(user.id, userId) ifSome { u =>
      OkApiResult(UnbanUserResult(OK))
    }
  }

  /**
   * Ask person to become our friend.
   */
  def getBannedUsers(request: GetBannedUsersRequest): ApiResult[GetBannedUsersResult] = handleDbException {
    OkApiResult(GetBannedUsersResult(
      userIds = List.empty,
      pageSize = request.pageSize,
      hasMore = false))
  }
}

