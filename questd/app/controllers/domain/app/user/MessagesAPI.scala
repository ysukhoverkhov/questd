package controllers.domain.app.user

import components._
import controllers.domain._
import controllers.domain.app.protocol.ProfileModificationResult._
import controllers.domain.helpers._
import models.domain.common.ClientPlatform
import models.domain.user.User
import models.domain.user.message.Message
import play.Logger

case class SendMessageRequest(user: User, message: Message)
case class SendMessageResult(user: User)

case class BroadcastMessageRequest(message: Message)
case class BroadcastMessageResult()

case class RemoveMessageRequest(user: User, messageId: String)
case class RemoveMessageResult(allowed: ProfileModificationResult)

case class AddDeviceTokenRequest(user: User, platform: ClientPlatform.Value, token: String)
case class AddDeviceTokenResult(allowed: ProfileModificationResult)

case class RemoveDeviceTokenRequest(user: User, token: String)
case class RemoveDeviceTokenResult(allowed: ProfileModificationResult)


// TODO: rename me to Events API.
private[domain] trait MessagesAPI { this: DBAccessor =>

  /**
   * Sends message to a user.
   */ // TODO: make it "notify event what will decide should we sund push or generate message or both.
  def sendMessage(request: SendMessageRequest): ApiResult[SendMessageResult] = handleDbException {
    import request._

    def capMessages(u: User): User = {
      if (u.profile.messages.length >= logic.constants.NumberOfStoredMessages) {
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
   * Broadcast a message to every user.
   */
  def broadcastMessage(request: BroadcastMessageRequest): ApiResult[BroadcastMessageResult] =  handleDbException {
    import request._

    db.user.addMessageToEveryone(message)

    OkApiResult(BroadcastMessageResult())
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
   * Adds new device token for user to send notifications to.
   */
  def addDeviceToken(request: AddDeviceTokenRequest): ApiResult[AddDeviceTokenResult] = handleDbException {

//    db.user.removeMessage(user.id, messageId)

    OkApiResult(AddDeviceTokenResult(OK))
  }

  /**
   * Removes device token from list of revices to send notifications to.
   */
  def removeDeviceToken(request: RemoveDeviceTokenRequest): ApiResult[RemoveDeviceTokenResult] = handleDbException {

    //    db.user.removeMessage(user.id, messageId)

    OkApiResult(RemoveDeviceTokenResult(OK))
  }

}

