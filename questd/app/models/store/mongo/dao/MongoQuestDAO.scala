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
    status: List[QuestStatus.Value] = List.empty,
    authorIds: List[String] = List.empty,
    authorIdsExclude: List[String] = List.empty,
    levels: Option[(Int, Int)] = None,
    skip: Int = 0,
    vip: Option[Boolean] = None,
    ids: List[String] = List.empty,
    idsExclude: List[String] = List.empty,
    cultureId: Option[String] = None): Iterator[Quest] = {

    val queryBuilder = MongoDBObject.newBuilder

    if (status.length > 0) {
      queryBuilder += ("status" -> MongoDBObject("$in" -> status.map(_.toString)))
    }

    if (authorIds.length > 0) {
      queryBuilder += ("info.authorId" -> MongoDBObject("$in" -> authorIds))
    }

    if (authorIdsExclude.length > 0) {
      queryBuilder += ("info.authorId" -> MongoDBObject("$nin" -> authorIdsExclude))
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

    if (idsExclude.length > 0) {
      queryBuilder += ("id" -> MongoDBObject("$nin" -> idsExclude))
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
    likesChange: Int,
    votersCountChange: Int = 0,
    cheatingChange: Int = 0,

    spamChange: Int = 0,
    pornChange: Int = 0): Option[Quest] = {

    findAndModify(
      id,
      MongoDBObject(
        "$inc" -> MongoDBObject(
          "rating.points" -> pointsChange,
          "rating.likesCount" -> likesChange,
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
  def updateStatus(id: String, newStatus: QuestStatus.Value): Option[Quest] = {
    findAndModify(
      id,
      MongoDBObject(
        "$set" -> MongoDBObject(
          "status" -> newStatus.toString,
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
      updateRules = MongoDBObject(
        "$set" -> MongoDBObject(
          "cultureId" -> newCultureId)),
      multi = true)
  }

}

