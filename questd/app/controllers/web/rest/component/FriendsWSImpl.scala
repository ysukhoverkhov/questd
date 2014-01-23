package controllers.web.rest.component

import play.api._
import play.api.mvc._
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import controllers.web.rest.component.helpers._
import controllers.web.rest.protocol._
import controllers.domain.app.user._

trait FriendsWSImpl extends QuestController with SecurityWSImpl { this: WSComponent#WS =>

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
  
  def removeFromFriends = wrapJsonApiCallReturnBody[WSRemoveFromFriendsResult] { (js, r) =>
    val v = Json.read[WSRemoveFromFriendsRequest](js.toString)

    api.removeFromFriends(RemoveFromFriendsRequest(r.user, v.id))
  }

}

