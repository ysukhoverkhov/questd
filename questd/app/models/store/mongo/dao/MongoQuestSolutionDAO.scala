package models.store.mongo.dao

import play.Logger
import models.store.mongo.helpers._
import models.store.dao._
import models.store._
import models.domain._
import com.mongodb.casbah.commons.MongoDBObject
import java.util.Date

/**
 * DOA for Config objects
 */
private[mongo] class MongoQuestSolutionDAO
  extends BaseMongoDAO[QuestSolution](collectionName = "solutions")
  with QuestSolutionDAO {

  // TODO: remove these 3 functions.
  def allWithStatusAndLevels(status: String, minLevel: Int, maxLevel: Int): Iterator[QuestSolution] = {
    findByExample(
      MongoDBObject(
        ("status" -> status),
        ("$and" -> Array(
          MongoDBObject("questLevel" -> MongoDBObject("$gte" -> minLevel)),
          MongoDBObject("questLevel" -> MongoDBObject("$lte" -> maxLevel))))),
      MongoDBObject("lastModDate" -> 1))
  }

  def allWithParams(
    status: Option[String] = None,
    userIds: List[String] = List(),
    levels: Option[(Int, Int)] = None,
    skip: Int = 0,
    vip: Option[Boolean] = None,
    ids: List[String] = List(),
    questIds: List[String] = List()): Iterator[QuestSolution] = {

    val queryBuilder = MongoDBObject.newBuilder

    if (status != None) {
      queryBuilder += ("status" -> status.get)
    }

    if (userIds.length > 0) {
      queryBuilder += ("userId" -> MongoDBObject("$in" -> userIds))
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

    if (questIds.length > 0) {
      queryBuilder += ("questId" -> MongoDBObject("$in" -> questIds))
    }

    Logger.trace("MongoQuestSolutionDAO - allWithParams - " + queryBuilder.result);

    findByExample(
      queryBuilder.result,
      MongoDBObject("lastModDate" -> 1),
      skip)
  }

  def updateStatus(id: String, newStatus: String): Option[QuestSolution] = {
    findAndModify(
      id,
      MongoDBObject(
        ("$set" -> MongoDBObject(
          "status" -> newStatus,
          "lastModDate" -> new Date()))))
  }

  def updatePoints(
    id: String,

    reviewsCountChange: Int = 0,
    pointsRandomChange: Int = 0,
    pointsFriendsChange: Int = 0,
    pointsInvitedChange: Int = 0,
    cheatingChange: Int = 0,

    spamChange: Int = 0,
    pornChange: Int = 0): Option[QuestSolution] = {

    findAndModify(
      id,
      MongoDBObject(
        ("$inc" -> MongoDBObject(
          "rating.reviewsCount" -> reviewsCountChange,
          "rating.pointsRandom" -> pointsRandomChange,

          "rating.pointsFriends" -> pointsFriendsChange,
          "rating.pointsInvited" -> pointsInvitedChange,

          "rating.cheating" -> cheatingChange,

          "rating.iacpoints.spam" -> spamChange,
          "rating.iacpoints.porn" -> pornChange)),
        ("$set" -> MongoDBObject(
          "lastModDate" -> new Date()))))
  }

}

