package controllers.web.rest.component

import controllers.web.rest.component.helpers._
import controllers.web.rest.protocol._
import controllers.domain.app.user._

trait FollowingWSImpl extends QuestController with SecurityWSImpl { this: WSComponent#WS =>


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

}

