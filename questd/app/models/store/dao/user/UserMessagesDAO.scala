package models.store.dao.user

import java.util.Date

import models.domain.user.User
import models.domain.user.devices.Device
import models.domain.user.message.Message

/**
 * DAO related to messages.
 */
trait UserMessagesDAO {

  def addMessage(id: String, message: Message): Option[User]

  def addMessageToEveryone(message: Message): Unit

  def removeOldestMessage(id: String): Option[User]

  def removeMessage(id: String, messageId: String): Option[User]

  def addDevice(id: String, device: Device): Option[User]

  def removeDevice(id: String, token: String): Option[User]

  /**
   * Sets time of last sent notification.
   *
   * @param id Id of a user to set data to.
   * @param time Time to set to.
   * @return Updated user.
   */
  def setNotificationSentTime(id: String, time: Date): Option[User]
}
