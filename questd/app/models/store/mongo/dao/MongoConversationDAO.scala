package models.store.mongo.dao

import models.domain.chat.Conversation
import models.store.dao._
import models.store.mongo.helpers._

/**
 * DOA for Chat conversiations objects objects
 */
private[mongo] class MongoConversationDAO
  extends BaseMongoDAO[Conversation](collectionName = "conversations")
  with ConversationDAO {

}

