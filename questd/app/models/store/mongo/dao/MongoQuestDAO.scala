package models.store.mongo.dao

import java.util.Date

import com.mongodb.casbah.commons.MongoDBObject
import models.domain.quest.{Quest, QuestStatus}
import models.store.dao._
import models.store.mongo.helpers._

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
    cultureId: Option[String] = None,
    withSolutions: Option[Boolean] = None): Iterator[Quest] = {

    val queryBuilder = MongoDBObject.newBuilder

    if (status.nonEmpty) {
      queryBuilder += ("status" -> MongoDBObject("$in" -> status.map(_.toString)))
    }

    if (authorIds.nonEmpty) {
      queryBuilder += ("info.authorId" -> MongoDBObject("$in" -> authorIds))
    }

    if (authorIdsExclude.nonEmpty) {
      queryBuilder += ("info.authorId" -> MongoDBObject("$nin" -> authorIdsExclude))
    }

    if (levels.isDefined) {
      queryBuilder += ("$and" -> Array(
        MongoDBObject("info.level" -> MongoDBObject("$gte" -> levels.get._1)),
        MongoDBObject("info.level" -> MongoDBObject("$lte" -> levels.get._2))))
    }

    if (vip.isDefined) {
      queryBuilder += ("info.vip" -> vip.get)
    }

    if (ids.nonEmpty) {
      queryBuilder += ("id" -> MongoDBObject("$in" -> ids))
    }

    if (idsExclude.nonEmpty) {
      queryBuilder += ("id" -> MongoDBObject("$nin" -> idsExclude))
    }

    if (cultureId.isDefined) {
      queryBuilder += ("cultureId" -> cultureId.get)
    }

    if (withSolutions.isDefined) {
      if (withSolutions.get)
        queryBuilder += ("solutionsCount" -> MongoDBObject("$gt" -> 0))
      else
        queryBuilder += ("solutionsCount" -> MongoDBObject("$eq" -> 0))
    }

    findByExample(
      queryBuilder.result(),
      MongoDBObject(
        "rating.timelinePoints" -> -1,
        "lastModDate" -> 1),
      skip)
  }

  def updatePoints(
    id: String,
    timelinePointsChange: Int,
    likesChange: Int,
    votersCountChange: Int = 0,
    cheatingChange: Int = 0,

    spamChange: Int = 0,
    pornChange: Int = 0): Option[Quest] = {

    findAndModify(
      id,
      MongoDBObject(
        "$inc" -> MongoDBObject(
          "rating.timelinePoints" -> timelinePointsChange,
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
   * @inheritdoc
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

  /**
   * @inheritdoc
   */
  def addSolution(id: String): Option[Quest] = {
    findAndModify(
      id,
      MongoDBObject(
        "$inc" -> MongoDBObject(
          "solutionsCount" -> 1)))
  }
}

