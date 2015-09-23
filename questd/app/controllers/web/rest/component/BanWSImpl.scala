package controllers.web.rest.component

import controllers.web.helpers._

private object BanWSImplTypes {

  /**
   * @param userId Id of a person to add to ban.
   */
  case class WSBanUserRequest(
    userId: String)
  type WSBanUserResult = BanUserResult

  /**
   * @param userId Id of a person to remove ban from.
   */
  case class WSUnbanUserRequest(
    userId: String)
  type WSUnbanUserResult = UnbanUserResult

  case class WSGetBannedUsersRequest(
    pageNumber: Int,
    pageSize: Int)
  type WSGetBannedUsersResult = GetBannedUsersResult
}

trait BanWSImpl extends QuestController with SecurityWSImpl { this: WSComponent#WS =>

  import BanWSImplTypes._

  def banUser = wrapJsonApiCallReturnBody[WSBanUserResult] { (js, r) =>
    val v = Json.read[WSBanUserRequest](js.toString)

    api.banUser(BanUserRequest(r.user, v.userId))
  }

  def unbanUser = wrapJsonApiCallReturnBody[WSUnbanUserResult] { (js, r) =>
    val v = Json.read[WSUnbanUserRequest](js.toString)

    api.unbanUser(UnbanUserRequest(r.user, v.userId))
  }

  def getBannedUsers = wrapJsonApiCallReturnBody[WSGetBannedUsersResult] { (js, r) =>
    val v = Json.read[WSGetBannedUsersRequest](js.toString)

    api.getBannedUsers(GetBannedUsersRequest(r.user, v.pageNumber, v.pageSize))
  }
}

