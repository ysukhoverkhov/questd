package controllers.web.rest.component

import controllers.domain.app.user._
import controllers.web.helpers._

private object MessagesWSImplTypes {

  case class WSRemoveMessageRequest(
    /// Id of a message to remove.
    id: String)
  type WSRemoveMessageResult = RemoveMessageResult
}

trait MessagesWSImpl extends QuestController with SecurityWSImpl { this: WSComponent#WS =>
  import MessagesWSImplTypes.{WSRemoveMessageRequest, WSRemoveMessageResult}

  def removeMessage() = wrapJsonApiCallReturnBody[WSRemoveMessageResult] { (js, r) =>
    val v = Json.read[WSRemoveMessageRequest](js.toString)

    api.removeMessage(RemoveMessageRequest(r.user, v.id))
  }
}

