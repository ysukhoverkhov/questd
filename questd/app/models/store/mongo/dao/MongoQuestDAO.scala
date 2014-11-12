package models.store.mongo.dao

import play.Logger
import models.store.mongo.helpers._
import models.store.dao._
import models.domain._
import com.mongodb.casbah.commons.MongoDBObject
import java.util.Date

/**
 * DOA for Quest objects
 */
private[mongo] class MongoQuestDAO
  extends BaseMongoDAO[Quest](collectionName = "quests")
  with QuestDAO {

  def countWithStatus(status: String): Long = {
    countByExample(
      MongoDBObject("status" -> status))
  }

  def allWithParams(
    status: List[String] = List(),
    authorIds: List[String] = List(),
    levels: Option[(Int, Int)] = None,
    skip: Int = 0,
    vip: Option[Boolean] = None,
    ids: List[String] = List(),
    cultureId: Option[String] = None): Iterator[Quest] = {

    val queryBuilder = MongoDBObject.newBuilder

    if (status.length > 0) {
      queryBuilder += ("status" -> MongoDBObject("$in" -> status))
    }

    if (authorIds.length > 0) {
      queryBuilder += ("info.authorId" -> MongoDBObject("$in" -> authorIds))
    }

    if (levels != None) {
      queryBuilder += ("$and" -> Array(
        MongoDBObject("info.level" -> MongoDBObject("$gte" -> levels.get._1)),
        MongoDBObject("info.level" -> MongoDBObject("$lte" -> levels.get._2))))
    }

    if (vip != None) {
      queryBuilder += ("info.vip" -> vip.get)
    }

    if (ids.length > 0) {
      queryBuilder += ("id" -> MongoDBObject("$in" -> ids))
    }

    if (cultureId != None) {
      queryBuilder += ("cultureId" -> cultureId.get)
    }

    Logger.trace("DB - allWithParams - " + queryBuilder.result)

    findByExample(
      queryBuilder.result(),
      MongoDBObject(
        "rating.points" -> -1,
        "lastModDate" -> 1),
      skip)
  }

  def updatePoints(
    id: String,
    pointsChange: Int,
    votersCountChange: Int = 0,
    cheatingChange: Int = 0,

    spamChange: Int = 0,
    pornChange: Int = 0): Option[Quest] = {

    findAndModify(
      id,
      MongoDBObject(
        "$inc" -> MongoDBObject(
          "rating.points" -> pointsChange,
          "rating.votersCount" -> votersCountChange,
          "rating.cheating" -> cheatingChange,

          "rating.iacpoints.spam" -> spamChange,
          "rating.iacpoints.porn" -> pornChange),
        "$set" -> MongoDBObject(
          "lastModDate" -> new Date())))
  }

  /**
   *
   */
  def updateStatus(id: String, newStatus: String): Option[Quest] = {
    findAndModify(
      id,
      MongoDBObject(
        "$set" -> MongoDBObject(
          "status" -> newStatus,
          "lastModDate" -> new Date())))
  }

  /**
   *
   */
  def updateInfo(id: String, newLevel: Int): Option[Quest] = {
    findAndModify(
      id,
      MongoDBObject(
        "$set" -> MongoDBObject(
          "info.level" -> newLevel,
          "lastModDate" -> new Date())))
  }

  /**
   *
   */
  def replaceCultureIds(oldCultureId: String, newCultureId: String): Unit = {
    update(
      query = MongoDBObject(
        "cultureId" -> oldCultureId),
      u = MongoDBObject(
        "$set" -> MongoDBObject(
          "cultureId" -> newCultureId)),
      multi = true)
  }

}

