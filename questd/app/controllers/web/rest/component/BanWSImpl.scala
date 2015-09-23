package controllers.web.rest.component

import controllers.domain.app.user._
import controllers.web.helpers._
import controllers.web.rest.component.FriendsWSImplTypes.WSGetFriendsResult

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

    api.costToRequestFriendship(CostToRequestFriendshipRequest(r.user, v.id))
  }

  def unbanUser = wrapJsonApiCallReturnBody[WSUnbanUserResult] { (js, r) =>
    val v = Json.read[WSUnbanUserRequest](js.toString)

    api.askFriendship(AskFriendshipRequest(r.user, v.id))
  }

  def getBannedUsers = wrapJsonApiCallReturnBody[WSGetBannedUsersResult] { (js, r) =>
    api.getFriends(GetFriendsRequest(r.user))
  }
}

