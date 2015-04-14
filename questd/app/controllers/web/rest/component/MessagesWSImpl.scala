package controllers.web.rest.component

import controllers.domain.app.user._
import controllers.web.rest.component.helpers._

private object MessagesWSImpl {
  type WSGetMessagesResult = GetMessagesResult

  case class WSRemoveMessageRequest(
    /// Id of a message to remove.
    id: String)
  type WSRemoveMessageResult = RemoveMessageResult
}

trait MessagesWSImpl extends QuestController with SecurityWSImpl { this: WSComponent#WS =>

  import MessagesWSImpl._

  def getMessages = wrapApiCallReturnBody[WSGetMessagesResult] { r =>
    api.getMessages(GetMessagesRequest(r.user))
  }

  def removeMessage() = wrapJsonApiCallReturnBody[WSRemoveMessageResult] { (js, r) =>
    val v = Json.read[WSRemoveMessageRequest](js.toString)

    api.removeMessage(RemoveMessageRequest(r.user, v.id))
  }


}

