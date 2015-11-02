package models.store.mongo.dao.user
import java.util.Date

import com.mongodb.casbah.commons.MongoDBObject
import com.novus.salat._
import models.domain.user.User
import models.domain.user.devices.Device
import models.domain.user.message.Message
import models.store.dao.user.UserMessagesDAO
import models.store.mongo.helpers.BaseMongoDAO
import models.store.mongo.SalatContext._

/**
 * Mongo DAO implementation.
 */
trait MongoUserMessagesDAO extends UserMessagesDAO {
  this: BaseMongoDAO[User] =>

  /**
   *
   */
  def addMessage(id: String, message: Message): Option[User] = {
    findAndModify(
      id,
      MongoDBObject(
        "$push" -> MongoDBObject(
          "profile.messages" -> grater[Message].asDBObject(message))))
  }

  /**
   * @inheritdoc
   */
  def addMessageToEveryone(message: Message): Unit = {
    update(
      query = MongoDBObject(),
      updateRules = MongoDBObject(
        "$push" -> MongoDBObject(
          "profile.messages" -> grater[Message].asDBObject(message))),
      multi = true)
  }

  /**
   *
   */
  def removeOldestMessage(id: String): Option[User] = {
    findAndModify(
      id,
      MongoDBObject(
        "$pop" -> MongoDBObject(
          "profile.messages" -> -1)))
  }

  /**
   * @inheritdoc
   */
  def removeMessage(id: String, messageId: String): Option[User] = {
    findAndModify(
      id,
      MongoDBObject(
        "$pull" -> MongoDBObject(
          "profile.messages" -> MongoDBObject("id" -> messageId))))
  }

  /**
   * @inheritdoc
   */
  def addDevice(id: String, device: Device): Option[User] = {
    findAndModify(
      id,
      MongoDBObject(
        "$addToSet" -> MongoDBObject(
          "devices" -> grater[Device].asDBObject(device))))
  }

  /**
   * @inheritdoc
   */
  def removeDevice(id: String, token: String): Option[User] = {
    findAndModify(
      id,
      MongoDBObject(
        "$pull" -> MongoDBObject(
          "devices" -> MongoDBObject("token" -> token))))
  }

  /**
   * @inheritdoc
   */
  def setNotificationSentTime(id: String, time: Date): Option[User] = {
    findAndModify(
      id,
      MongoDBObject(
        "$set" -> MongoDBObject(
          "schedules.lastNotificationSentAt" -> time)))
  }
}
