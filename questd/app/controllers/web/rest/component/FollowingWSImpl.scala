package controllers.web.rest.component

import controllers.domain.app.user._
import controllers.web.helpers._

private object FollowingWSImplTypes {

  type WSGetFollowingResult = GetFollowingResult

  type WSGetFollowersResult = GetFollowersResult

  type WSCostToFollowingResult = CostToFollowingResult

  case class WSAddToFollowingRequest(
    /// Id of a person to add.
    id: String)

  type WSAddToFollowingResult = AddToFollowingResult

  case class WSRemoveFromFollowingRequest(
    /// Id of a person to remove.
    id: String)

  type WSRemoveFromFollowingResult = RemoveFromFollowingResult


  case class WSGetSuggestsForFollowingRequest(
    tokens: Map[String, String])
  type WSGetSuggestsForFollowingResult = GetSuggestsForFollowingResult

  case class WSGetSNFriendsInGameRequest(
    tokens: Map[String, String])
  type WSGetSNFriendsInGameResult = GetSNFriendsInGameResult
}

trait FollowingWSImpl extends BaseController with SecurityWSImpl { this: WSComponent#WS =>

  import controllers.web.rest.component.FollowingWSImplTypes._

  def getFollowing = wrapApiCallReturnBody[WSGetFollowingResult] { r =>
    api.getFollowing(GetFollowingRequest(r.user))
  }

  def getFollowers = wrapApiCallReturnBody[WSGetFollowersResult] { r =>
    api.getFollowers(GetFollowersRequest(r.user))
  }

  def costToFollow = wrapApiCallReturnBody[WSCostToFollowingResult] { r =>
    api.costToFollowing(CostToFollowingRequest(r.user))
  }

  def addToFollowing() = wrapJsonApiCallReturnBody[WSAddToFollowingResult] { (js, r) =>
    val v = Json.read[WSAddToFollowingRequest](js.toString)

    api.addToFollowing(AddToFollowingRequest(r.user, v.id))
  }

  def removeFromFollowing() = wrapJsonApiCallReturnBody[WSRemoveFromFollowingResult] { (js, r) =>
    val v = Json.read[WSRemoveFromFollowingRequest](js.toString)

    api.removeFromFollowing(RemoveFromFollowingRequest(r.user, v.id))
  }

  def getSuggestsForFollowing = wrapJsonApiCallReturnBody[WSGetSuggestsForFollowingResult] { (js, r) =>
    val v = Json.read[WSGetSuggestsForFollowingRequest](js.toString)

    api.getSuggestsForFollowing(GetSuggestsForFollowingRequest(r.user, v.tokens))
  }

  def getSNFriendsInGame = wrapJsonApiCallReturnBody[WSGetSNFriendsInGameResult] { (js, r) =>
    val v = Json.read[WSGetSNFriendsInGameRequest](js.toString)

    api.getSNFriendsInGame(GetSNFriendsInGameRequest(r.user, v.tokens))
  }



}

