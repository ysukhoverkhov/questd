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
  def updatePoints(
    id: String,
    pointsChange: Int,
    votersCountChange: Int,
    cheatingChange: Int = 0,

    spamChange: Int = 0,
    pornChange: Int = 0,

    easyChange: Int = 0,
    normalChange: Int = 0,
    hardChange: Int = 0,
    extremeChange: Int = 0,

    minsChange: Int = 0,
    hourChange: Int = 0,
    dayChange: Int = 0,
    daysChange: Int = 0,
    weekChange: Int = 0): Option[Quest] = {

    findAndModify(
      id,
      MongoDBObject(
        ("$inc" -> MongoDBObject(
          "rating.points" -> pointsChange,
          "rating.votersCount" -> votersCountChange,
          "rating.cheating" -> cheatingChange,

          "rating.iacpoints.spam" -> spamChange,
          "rating.iacpoints.porn" -> pornChange,

          "rating.difficultyRating.easy" -> easyChange,
          "rating.difficultyRating.normal" -> normalChange,
          "rating.difficultyRating.hard" -> hardChange,
          "rating.difficultyRating.extreme" -> extremeChange,

          "rating.durationRating.mins" -> minsChange,
          "rating.durationRating.hour" -> hourChange,
          "rating.durationRating.day" -> dayChange,
          "rating.durationRating.days" -> daysChange,
          "rating.durationRating.week" -> weekChange))))
  }

  /**
   *
   */
  def updateStatus(id: String, newStatus: String): Option[Quest] = {
    findAndModify(
      id,
      MongoDBObject(
        ("$set" -> MongoDBObject(
          "status" -> newStatus))))
  }

  /**
   *
   */
  def updateLevel(id: String, newLevel: Int): Option[Quest] = {
    findAndModify(
      id,
      MongoDBObject(
        ("$set" -> MongoDBObject(
          "info.level" -> newLevel))))
  }
}

