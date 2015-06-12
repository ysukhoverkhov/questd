package models.store.mongo.dao

import java.util.Date

import com.mongodb.casbah.commons.MongoDBObject
import models.domain.solution.{Solution, SolutionStatus}
import models.store.dao._
import models.store.mongo.helpers._
import play.Logger

/**
 * DOA for Quest solution objects
 */
private[mongo] class MongoSolutionDAO
  extends BaseMongoDAO[Solution](collectionName = "solutions")
  with SolutionDAO {

  def allWithParams(
    status: List[SolutionStatus.Value] = List.empty,
    authorIds: List[String] = List.empty,
    authorIdsExclude: List[String] = List.empty,
    levels: Option[(Int, Int)] = None,
    skip: Int = 0,
    vip: Option[Boolean] = None,
    ids: List[String] = List.empty,
    idsExclude: List[String] = List.empty,
    questIds: List[String] = List.empty,
    themeIds: List[String] = List.empty,
    cultureId: Option[String] = None): Iterator[Solution] = {

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
        MongoDBObject("questLevel" -> MongoDBObject("$gte" -> levels.get._1)),
        MongoDBObject("questLevel" -> MongoDBObject("$lte" -> levels.get._2))))
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

    if (questIds.nonEmpty) {
      queryBuilder += ("info.questId" -> MongoDBObject("$in" -> questIds))
    }

    if (themeIds.nonEmpty) {
      queryBuilder += ("info.themeId" -> MongoDBObject("$in" -> themeIds))
    }

    if (cultureId.isDefined) {
      queryBuilder += ("cultureId" -> cultureId.get)
    }

    Logger.trace("MongoQuestSolutionDAO - allWithParams - " + queryBuilder.result)

    findByExample(
      queryBuilder.result(),
      MongoDBObject("lastModDate" -> 1),
      skip)
  }

  def updateStatus(
    id: String,
    newStatus: SolutionStatus.Value): Option[Solution] = {

    val queryBuilder = MongoDBObject.newBuilder

    queryBuilder +=
      ("$set" -> MongoDBObject(
        "status" -> newStatus.toString,
        "lastModDate" -> new Date()))

    findAndModify(
      id,
      queryBuilder.result())
  }

  def addParticipatedBattle(
    id: String,
    battleId: String): Option[Solution] = {

    val queryBuilder = MongoDBObject.newBuilder

    queryBuilder +=
      ("$push" -> MongoDBObject(
        "battleIds" -> battleId))

    findAndModify(
      id,
      queryBuilder.result())
  }


  def updatePoints(
    id: String,
    timelinePointsChange: Int,
    likesChange: Int,
    votersCountChange: Int = 0,
    cheatingChange: Int = 0,

    spamChange: Int = 0,
    pornChange: Int = 0): Option[Solution] = {

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

