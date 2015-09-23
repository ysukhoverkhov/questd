package controllers.domain.app.user

import components._
import controllers.domain.app.protocol.ProfileModificationResult._
import controllers.domain.helpers._
import controllers.domain.{DomainAPIComponent, _}
import models.domain.user._


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
    OkApiResult(BanUserResult(OK))
  }

  /**
   * Unban user we've banned.
   */
  def unbanUser(request: UnbanUserRequest): ApiResult[UnbanUserResult] = handleDbException {
    OkApiResult(UnbanUserResult(OK))
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

