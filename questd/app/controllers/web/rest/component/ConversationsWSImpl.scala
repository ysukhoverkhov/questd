package controllers.web.rest.component

import java.util.Date

import controllers.domain.app.user._
import controllers.web.helpers._

private object ConversationsWSImplTypes {

  import scala.language.implicitConversions

  case class WSCreateConversationRequest(
    peerId: String)
  type WSCreateConversationResult = CreateConversationResult

  case class WSLeaveConversationRequest(
    conversationId: String)
  type WSLeaveConversationResult = LeaveConversationResult


  type WSGetMyConversationsResult = GetMyConversationsResult


  case class WSSendChatMessageRequest(
    conversationId: String,
    message: String)
  type WSSendChatMessageResult = SendChatMessageResult


  case class WSGetChatMessagesRequest(
    conversationId: String,
    fromDate: Date,
    count: Int)
  type WSGetChatMessagesResult = GetChatMessagesResult

}

trait ConversationsWSImpl extends BaseController with SecurityWSImpl with CommonFunctions { this: WSComponent#WS =>

  import controllers.web.rest.component.ConversationsWSImplTypes._

  def createConversation = wrapJsonApiCallReturnBody[WSCreateConversationResult] { (js, r) =>
    val v = Json.read[WSCreateConversationRequest](js)

    api.createConversation(CreateConversationRequest(r.user, v.peerId))
  }

  def leaveConversation = wrapJsonApiCallReturnBody[WSLeaveConversationResult] { (js, r) =>
    val v = Json.read[WSLeaveConversationRequest](js)

    api.leaveConversation(LeaveConversationRequest(r.user, v.conversationId))
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

