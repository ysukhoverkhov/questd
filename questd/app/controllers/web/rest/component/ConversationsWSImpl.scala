package controllers.web.rest.component

import controllers.domain.app.user._
import controllers.web.helpers._
import controllers.web.rest.component.CreateQuestWSImplTypes.{WSCreateQuestResult, WSCreateQuestRequest}
import models.domain.quest.QuestInfoContent
import play.api.libs
import play.api.libs.ws

private object ConversationsWSImplTypes {

  import scala.language.implicitConversions

  type WSCreateConversationResult = CreateConversationResult

  case class WSCreateConversationRequest(
    peerId: String)


  type WSGetMyConversationsResult = GetMyConversationsResult

}

trait ConversationsWSImpl extends QuestController with SecurityWSImpl with CommonFunctions { this: WSComponent#WS =>

  import controllers.web.rest.component.ConversationsWSImplTypes._


  def createConversation = wrapJsonApiCallReturnBody[WSCreateConversationResult] { (js, r) =>
    val v = Json.read[WSCreateConversationRequest](js)

    api.createConversation(CreateConversationRequest(r.user, v.peerId))
  }

  def getMyConversations = wrapApiCallReturnBody[WSGetMyConversationsResult] { r =>
    api.getMyConversations(GetMyConversationsRequest(r.user))
  }
}

