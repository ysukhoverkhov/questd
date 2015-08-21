package controllers.web.rest.component

import java.util.Date

import controllers.domain.app.user._
import controllers.web.helpers._

private object ConversationsWSImplTypes {

  import scala.language.implicitConversions

  type WSCreateConversationResult = CreateConversationResult

  case class WSCreateConversationRequest(
    peerId: String)


  type WSGetMyConversationsResult = GetMyConversationsResult


  type WSSendChatMessageResult = SendChatMessageResult

  case class WSSendChatMessageRequest(
    conversationId: String,
    message: String)


  type WSGetChatMessagesResult = GetChatMessagesResult

  case class WSGetChatMessagesRequest(
    conversationId: String,
    fromDate: Date,
    count: Int)

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

  def sendChatMessage = wrapJsonApiCallReturnBody[WSSendChatMessageResult] { (js, r) =>
    val v = Json.read[WSSendChatMessageRequest](js)

    api.sendChatMessage(SendChatMessageRequest(r.user, v.conversationId, v.message))
  }

  def getChatMessages = wrapJsonApiCallReturnBody[WSGetChatMessagesResult] { (js, r) =>
    val v = Json.read[WSGetChatMessagesRequest](js)

    api.getChatMessages(GetChatMessagesRequest(r.user, v.conversationId, v.fromDate, v.count))
  }
}

