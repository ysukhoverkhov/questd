package controllers.domain.app.user

import java.util.Date

import components._
import controllers.domain._
import controllers.domain.app.protocol.CommonCode
import controllers.domain.helpers._
import controllers.services.devicenotifications.DeviceNotifications
import models.domain.common.ClientPlatform
import models.domain.user.User
import models.domain.user.message.{Message, MessageMetaInfo}
import play.Logger


case class SendMessageRequest(user: User, message: Message)
case class SendMessageResult(user: User)


case class BroadcastMessageRequest(message: Message)
case class BroadcastMessageResult()


object RemoveMessageCode extends Enumeration with CommonCode
case class RemoveMessageRequest(user: User, messageId: String)
case class RemoveMessageResult(allowed: RemoveMessageCode.Value)


object AddDeviceTokenCode extends Enumeration with CommonCode
case class AddDeviceTokenRequest(user: User, platform: ClientPlatform.Value, token: String)
case class AddDeviceTokenResult(allowed: AddDeviceTokenCode.Value)


object RemoveDeviceTokenCode extends Enumeration with CommonCode
case class RemoveDeviceTokenRequest(user: User, token: String)
case class RemoveDeviceTokenResult(allowed: RemoveDeviceTokenCode.Value)


case class CheckSendNotificationsRequest(user: User)
case class CheckSendNotificationsResult(user: User)


case class NotifyWithMessageRequest(user: User, message: Message, numberOfEvents: Int)
case class NotifyWithMessageResult(user: User)


private[domain] trait EventsAPI { this: DomainAPIComponent#DomainAPI with DBAccessor =>

  /**
   * Sends message to a user.
   * This is used as entry point for all events.
   * We here check should we ignore the message or should not and add it if should not.
   * After that we asks to send notifications if it's required.
   */
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
      checkSendNotifications(CheckSendNotificationsRequest(u)) map { r =>
        OkApiResult(SendMessageResult(user = r.user))
      }
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
    import RemoveMessageCode._
    import request._

    db.user.removeMessage(user.id, messageId)

    OkApiResult(RemoveMessageResult(OK))
  }

  /**
   * Adds new device token for user to send notifications to.
   */
  def addDeviceToken(request: AddDeviceTokenRequest): ApiResult[AddDeviceTokenResult] = handleDbException {
    import AddDeviceTokenCode._
    import models.domain.user.devices.Device
    import request._

    db.user.addDevice(user.id, Device(platform, token)) ifSome { user =>
      OkApiResult(AddDeviceTokenResult(OK))
    }
  }

  /**
   * Removes device token from list of devices to send notifications to.
   */
  def removeDeviceToken(request: RemoveDeviceTokenRequest): ApiResult[RemoveDeviceTokenResult] = handleDbException {
    import RemoveDeviceTokenCode._
    import request._

    db.user.removeDevice(user.id, token) ifSome { user =>
      OkApiResult(RemoveDeviceTokenResult(OK))
    }
  }

  /**
   * Checks should we send notification or not and if we should sends it.
   */
  def checkSendNotifications(request: CheckSendNotificationsRequest): ApiResult[CheckSendNotificationsResult] = handleDbException {
    import request._

    if (!user.shouldSendNotification) {
      // Not now.
      OkApiResult(CheckSendNotificationsResult(user))
    } else if (user.profile.messages.isEmpty) {
      // Nothing to send.
      OkApiResult(CheckSendNotificationsResult(user))
    } else {
      notifyWithMessage(NotifyWithMessageRequest(
        user = user,
        message = user.profile.messages
          .dropWhile(_.generatedAt.before(user.schedules.lastNotificationSentAt))
          .sortBy[Int]{MessageMetaInfo.messagePriority(m.messageType)}
          .head,
        numberOfEvents = user.profile.messages.length
      )) map { r =>
        OkApiResult(CheckSendNotificationsResult(r.user))
      }
    }
  }

  /**
   * Notify user with a message.
   */
  def notifyWithMessage(request: NotifyWithMessageRequest): ApiResult[NotifyWithMessageResult] = handleDbException {
    import controllers.services.devicenotifications.DeviceNotifications.{Device, IOSDevice}
    import request._

    db.user.setNotificationSentTime(user.id, new Date()) ifSome { user =>
      val devices: Set[Device] = user.devices.map {
        case models.domain.user.devices.Device(ClientPlatform.iPhone, token) => IOSDevice(token)
      }.toSet[Device]

      val messageText = MessageMetaInfo.messageLocalizedMessage(message.messageType)

      deviceNotifications.actor ! DeviceNotifications.PushMessage(
        devices = DeviceNotifications.Devices(devices.toSet),
        message = messageText,
        badge = Some(numberOfEvents),
        sound = None,
        destinations = List(DeviceNotifications.MobileDestination)
      )

      OkApiResult(NotifyWithMessageResult(user))
    }
  }
}

