package models.store.mongo.dao

import java.util.Date

import com.mongodb.casbah.commons.MongoDBObject
import models.domain.chat.ChatMessage
import models.store.dao._
import models.store.mongo.helpers._

/**
 * DOA for Chat Messages objects objects
 */
private[mongo] class MongoChatMessageDAO
  extends BaseMongoDAO[ChatMessage](collectionName = "chats")
  with ChatMessageDAO {

  /**
   * @inheritdoc
   */
  def getForConversation(
    conversationId: String,
    since: Date): Iterator[ChatMessage] = {

    val queryBuilder = MongoDBObject.newBuilder

    queryBuilder += (
      "conversationId" -> conversationId)

    queryBuilder += (
      "creationDate" -> MongoDBObject("$gte" -> since))

    findByExample(
      queryBuilder.result(),
      MongoDBObject(
        "creationDate" -> 1))

    findByExample(
      queryBuilder.result())
  }
}

