package models.store.mongo.dao

import com.mongodb.casbah.commons.MongoDBObject
import models.domain.chat.Conversation
import models.store.dao._
import models.store.mongo.helpers._

/**
 * DOA for Chat conversations objects objects
 */
private[mongo] class MongoConversationDAO
  extends BaseMongoDAO[Conversation](collectionName = "conversations")
  with ConversationDAO {

  /**
   * @inheritdoc
   */
  def findByParticipant(participantId: String): Iterator[Conversation] = {

    val queryBuilder = MongoDBObject.newBuilder

    queryBuilder += (
      "participants.userId" -> participantId)

    findByExample(
      queryBuilder.result())
  }

  /**
   * @inheritdoc
   */
  def findByAllParticipants(participantIds: Seq[String]): Iterator[Conversation] = {
    val queryBuilder = MongoDBObject.newBuilder

    queryBuilder += (
      "participants.userId" -> MongoDBObject(
        "$all" -> participantIds
      ))

    findByExample(
      queryBuilder.result())
  }

  /**
   * @inheritdoc
   */
  def setUnreadMessagesFlag(id: String, userId: String, flag: Boolean): Option[Conversation] = {
    findAndModify(
      MongoDBObject(
        "id" -> id,
        "participants.userId" -> userId),
      MongoDBObject(
        "$set" -> MongoDBObject(
          "participants.$.hasUnreadMessages" -> flag)))
  }

  /**
   * @inheritdoc
   */
  def removeParticipant(id: String, userId: String): Option[Conversation] = {
    findAndModify(
      MongoDBObject(
        "id" -> id),
      MongoDBObject(
        "$pull" -> MongoDBObject(
          "participants" -> MongoDBObject("userId" -> userId))))
  }
}

