package controllers.web.rest.component

import controllers.domain.app.user._
import controllers.web.rest.component.helpers._

private object FriendsWSImplTypes {
  type WSGetFriendsResult = GetFriendsResult

  case class WSCostToRequestFriendshipRequest(
    /// Id of a person to add to friends.
    id: String)
  type WSCostToRequestFriendshipResult = CostToRequestFriendshipResult

  case class WSAskFriendshipRequest(
    /// Id of a person to add to friends.
    id: String)
  type WSAskFriendshipResult = AskFriendshipResult

  case class WSRespondFriendshipRequest(
    /// Id of a person we respond to.
    id: String,
    accepted: Boolean)
  type WSRespondFriendshipResult = RespondFriendshipResult

  case class WSRemoveFromFriendsRequest(
    /// Id of a person to add to friends.
    id: String)
  type WSRemoveFromFriendsResult = RemoveFromFriendsResult
}

trait FriendsWSImpl extends QuestController with SecurityWSImpl { this: WSComponent#WS =>

  import FriendsWSImplTypes._

  def getFriends = wrapApiCallReturnBody[WSGetFriendsResult] { r =>
    api.getFriends(GetFriendsRequest(r.user))
  }

  def costToRequestFriendship = wrapJsonApiCallReturnBody[WSCostToRequestFriendshipResult] { (js, r) =>
    val v = Json.read[WSCostToRequestFriendshipRequest](js.toString)

    api.costToRequestFriendship(CostToRequestFriendshipRequest(r.user, v.id))
  }

  def askFriendship = wrapJsonApiCallReturnBody[WSAskFriendshipResult] { (js, r) =>
    val v = Json.read[WSAskFriendshipRequest](js.toString)

    api.askFriendship(AskFriendshipRequest(r.user, v.id))
  }

  def respondFriendship = wrapJsonApiCallReturnBody[WSRespondFriendshipResult] { (js, r) =>
    val v = Json.read[WSRespondFriendshipRequest](js.toString)

    api.respondFriendship(RespondFriendshipRequest(r.user, v.id, v.accepted))
  }

  def removeFromFriends() = wrapJsonApiCallReturnBody[WSRemoveFromFriendsResult] { (js, r) =>
    val v = Json.read[WSRemoveFromFriendsRequest](js.toString)

    api.removeFromFriends(RemoveFromFriendsRequest(r.user, v.id))
  }

}

