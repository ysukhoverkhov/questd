package controllers.web.rest.component

import play.api._
import play.api.mvc._
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import controllers.web.rest.component.helpers._
import controllers.web.rest.protocol._
import controllers.domain.app.user._

trait MessagesWSImpl extends QuestController with SecurityWSImpl { this: WSComponent#WS =>

  def getMessages = wrapApiCallReturnBody[WSGetMessagesResult] { r =>
    api.getMessages(GetMessagesRequest(r.user))
  }
    
  def removeMessage = wrapJsonApiCallReturnBody[WSCostToRequestFriendshipResult] { (js, r) =>
    val v = Json.read[WSCostToRequestFriendshipRequest](js.toString)

    api.costToRequestFriendship(CostToRequestFriendshipRequest(r.user, v.id))
  }


}

