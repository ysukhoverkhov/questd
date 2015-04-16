package controllers.domain.app.user

import models.domain._
import components._
import controllers.domain._
import controllers.domain.helpers._
import play.Logger
import controllers.domain.app.protocol.ProfileModificationResult._

case class SendMessageRequest(user: User, message: Message)
case class SendMessageResult(user: User)

case class RemoveMessageRequest(user: User, messageId: String)
case class RemoveMessageResult(allowed: ProfileModificationResult)

case class GetMessagesRequest(user: User)
case class GetMessagesResult(allowed: ProfileModificationResult, messages: List[Message])

private[domain] trait MessagesAPI { this: DBAccessor =>

  /**
   * Sends message to a user.
   */
  def sendMessage(request: SendMessageRequest): ApiResult[SendMessageResult] = handleDbException {
    import request._

    def capMessages(u: User): User = {
      if (u.messages.length >= logic.constants.NumberOfStoredMessages) {
        db.user.removeOldestMessage(u.id) match {
          case Some(us) => capMessages(us)
          case None =>
            Logger.error("Unable to find user for removing messages " + u.id)
            u
        }
      } else {
        u
      }
    }

    val u = capMessages(user)

    db.user.addMessage(u.id, message) ifSome { u =>
      OkApiResult(SendMessageResult(user = u))
    }
  }

  /**
   * Remove message from user's list of messages by message's id.
   */
  def removeMessage(request: RemoveMessageRequest): ApiResult[RemoveMessageResult] = handleDbException {
    import request._

    db.user.removeMessage(user.id, messageId)

    OkApiResult(RemoveMessageResult(OK))
  }

  /**
   * Get all messages of a user.
   */
  def getMessages(request: GetMessagesRequest): ApiResult[GetMessagesResult] = handleDbException {
    import request._

    OkApiResult(GetMessagesResult(OK, user.messages))
  }

}

