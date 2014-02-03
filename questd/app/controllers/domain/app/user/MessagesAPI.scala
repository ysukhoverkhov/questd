package controllers.domain.app.user

import models.domain._
import models.store._
import controllers.domain.DomainAPIComponent
import components._
import controllers.domain._
import controllers.domain.helpers.exceptionwrappers._
import logic._
import play.Logger

case class SendMessageRequest(user: User, message: Message)
case class SendMessageResult()

private[domain] trait MessagesAPI { this: DBAccessor =>

  /**
   * Sends message to a user.
   */
  def sendMessage(request: SendMessageRequest): ApiResult[SendMessageResult] = handleDbException {
    import request._

    def capMessages(u: User): User = {
      if (u.messages.length >= logic.constants.numberOfStoredMessages) {
        db.user.removeOldestMessage(u.id) match {
          case Some(us) => capMessages(us)
          case None => {
            Logger.error("Unable to find user for removing messages " + u.id)
            u
          }
        }
      } else {
        u
      }
    }

    val u = capMessages(user)

    db.user.addMessage(u.id, message)

    OkApiResult(Some(SendMessageResult()))
  }

}

