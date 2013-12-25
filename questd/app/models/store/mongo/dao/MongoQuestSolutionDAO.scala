package models.store.mongo.dao

import play.Logger
import models.store.mongo.helpers._
import models.store.dao._
import models.store._
import models.domain._
import com.mongodb.casbah.commons.MongoDBObject

/**
 * DOA for Config objects
 */
private[mongo] class MongoQuestSolutionDAO
  extends BaseMongoDAO[QuestSolution](collectionName = "solutions")
  with QuestSolutionDAO {

  def allWithStatusAndLevels(status: String, minLevel: Int, maxLevel: Int): Iterator[QuestSolution] = {
    allByExample(
      ("status" -> status),
      ("$and" -> Array(
        MongoDBObject("questLevel" -> MongoDBObject("$gte" -> minLevel)),
        MongoDBObject("questLevel" -> MongoDBObject("$lte" -> maxLevel)))))
  }

  def allWithStatusAndQuest(status: String, questId: String): Iterator[QuestSolution] = {
    allByExample(
      ("status" -> status),
      ("questID" -> questId))
  }

}

