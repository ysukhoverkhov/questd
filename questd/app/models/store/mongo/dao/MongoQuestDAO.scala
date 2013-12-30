package models.store.mongo.dao

import play.Logger
import models.store.mongo.helpers._
import models.store.dao._
import models.store._
import models.domain._
import com.mongodb.casbah.commons.MongoDBObject

/**
 * DOA for Quest objects
 */
private[mongo] class MongoQuestDAO
  extends BaseMongoDAO[Quest](collectionName = "quests")
  with QuestDAO {

  def allWithStatus(status: String, minLevel: Int, maxLevel: Int): Iterator[Quest] = {
    allByExample(
      ("status" -> status),
      ("$and" -> Array(
        MongoDBObject("info.level" -> MongoDBObject("$gte" -> minLevel)),
        MongoDBObject("info.level" -> MongoDBObject("$lte" -> maxLevel)))))
  }

  /**
   * 
   */
  def updatePoints(id: String, pointsChange: Int, votersCountChange: Int): Option[Quest] = {
    findAndModify(
      id,
      MongoDBObject(
        ("$inc" -> MongoDBObject(
          "rating.points" -> pointsChange,
          "rating.votersCount" -> votersCountChange))))
  }

}

