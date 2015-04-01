package models.store.mongo.dao

import play.Logger
import models.store.mongo.helpers._
import models.store.dao._
import models.domain._
import com.mongodb.casbah.commons.MongoDBObject
import java.util.Date

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
        MongoDBObject("questLevel" -> MongoDBObject("$gte" -> levels.get._1)),
        MongoDBObject("questLevel" -> MongoDBObject("$lte" -> levels.get._2))))
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

    if (questIds.length > 0) {
      queryBuilder += ("info.questId" -> MongoDBObject("$in" -> questIds))
    }

    if (themeIds.length > 0) {
      queryBuilder += ("info.themeId" -> MongoDBObject("$in" -> themeIds))
    }

    if (cultureId != None) {
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
    newStatus: SolutionStatus.Value,
    battleId: Option[String]): Option[Solution] = {

    val queryBuilder = MongoDBObject.newBuilder

    queryBuilder +=
      ("$set" -> MongoDBObject(
        "status" -> newStatus.toString,
        "lastModDate" -> new Date()))

    battleId match {
      case None =>
      case Some(bid) =>
        queryBuilder +=
          ("$push" -> MongoDBObject(
            "battleIds" -> bid))
    }

    findAndModify(
      id,
      queryBuilder.result())
  }

  def updatePoints(
    id: String,

    reviewsCountChange: Int = 0,
    pointsRandomChange: Int = 0,
    pointsFriendsChange: Int = 0,
    likesCountChange: Int = 0,
    cheatingChange: Int = 0,

    spamChange: Int = 0,
    pornChange: Int = 0): Option[Solution] = {

    findAndModify(
      id,
      MongoDBObject(
        "$inc" -> MongoDBObject(
          "rating.reviewsCount" -> reviewsCountChange,

          "rating.pointsRandom" -> pointsRandomChange,
          "rating.pointsFriends" -> pointsFriendsChange,

          "rating.likesCount" -> likesCountChange,

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
      u = MongoDBObject(
        "$set" -> MongoDBObject(
          "cultureId" -> newCultureId)),
      multi = true)
  }
}
