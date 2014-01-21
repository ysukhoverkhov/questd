package controllers.web.rest.component

import play.api._
import play.api.mvc._
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import controllers.web.rest.component.helpers._
import controllers.web.rest.protocol._
import controllers.domain.app.user._

trait FriendsWSImpl extends QuestController with SecurityWSImpl { this: WSComponent#WS =>

//  def costToShortlist = wrapApiCallReturnBody[WSCostToShortlistResult] { r =>
//    api.costToShortlist(CostToShortlistRequest(r.user))
//  }
//  
//  def addToShortlist = wrapJsonApiCallReturnBody[WSAddToShortlistResult] { (js, r) =>
//    val v = Json.read[WSAddToShortlistRequest](js.toString)
//
//    api.addToShortlist(AddToShortlistRequest(r.user, v.id))
//  }
  
  
  def getFriends = wrapApiCallReturnBody[WSGetFriendsResult] { r =>
    api.getFriends(GetFriendsRequest(r.user))
  }
    
  def costToRequestFriendship = wrapJsonApiCallReturnBody[WSCostToRequestFriendshipResult] { (js, r) =>
    val v = Json.read[WSAddToShortlistRequest](js.toString)

    api.costToRequestFriendship(CostToRequestFriendshipRequest(r.user, v.id))
  }

  
  def askFriendship = TODO
  def respondFriendsip = TODO
  def removeFromFriends = TODO

}

