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
   * Searches culture by containing country.
   */
  def findByParticipant(participantId: String): Iterator[Conversation] = { // TODO: test me.

    val queryBuilder = MongoDBObject.newBuilder

    queryBuilder += (
      "participants.userId" -> participantId)

    // TODO: clean me up.
//    (
//        "$elemMatch" -> MongoDBObject(
//          "solutionId" -> winnerSolutionId)))

    findByExample(
      queryBuilder.result())
  }

}

