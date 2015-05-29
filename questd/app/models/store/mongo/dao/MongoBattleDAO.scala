package models.store.mongo.dao

import java.util.Date

import com.mongodb.casbah.commons.MongoDBObject
import models.domain.battle.{Battle, BattleStatus}
import models.store.dao._
import models.store.mongo.helpers._
import play.Logger

/**
 * DOA for Battles
 */
private[mongo] class MongoBattleDAO
  extends BaseMongoDAO[Battle](collectionName = "battles")
  with BattleDAO {

  /**
   * @inheritdoc
   */
  def allWithParams(
    status: List[BattleStatus.Value] = List.empty,
    authorIds: List[String] = List.empty,
    authorIdsExclude: List[String] = List.empty,
    solutionIds: List[String] = List.empty,
    levels: Option[(Int, Int)] = None,
    skip: Int = 0,
    vip: Option[Boolean] = None,
    ids: List[String] = List.empty,
    idsExclude: List[String] = List.empty,
    cultureId: Option[String] = None): Iterator[Battle] = {

    val queryBuilder = MongoDBObject.newBuilder

    if (status.nonEmpty) {
      queryBuilder += ("info.status" -> MongoDBObject("$in" -> status.map(_.toString)))
    }

    if (authorIds.nonEmpty) {
      queryBuilder += ("info.battleSides.authorId" -> MongoDBObject("$in" -> authorIds))
    }

    if (solutionIds.nonEmpty) {
      queryBuilder += ("info.battleSides.solutionId" -> MongoDBObject("$in" -> solutionIds))
    }

    if (authorIdsExclude.nonEmpty) {
      queryBuilder += ("info.battleSides.authorId" -> MongoDBObject("$nin" -> authorIdsExclude))
    }

    if (levels.isDefined) {
      queryBuilder += ("$and" -> Array(
        MongoDBObject("level" -> MongoDBObject("$gte" -> levels.get._1)),
        MongoDBObject("level" -> MongoDBObject("$lte" -> levels.get._2))))
    }

    if (vip.isDefined) {
      queryBuilder += ("vip" -> vip.get)
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

    Logger.trace("MongoBattleDAO - allWithParams - " + queryBuilder.result)

    findByExample(
      example = queryBuilder.result(),
      sort = MongoDBObject(
        "lastModDate" -> 1),
      skip = skip)
  }

  /**
   * @inheritdoc
   */
  def updateStatus(
    id: String,
    newStatus: BattleStatus.Value,
    setWinnerSolutions: List[String] = List.empty): Option[Battle] = {

    // REFACTOR: remove "for" here and make atomic call when this will be implemented.  https://jira.mongodb.org/browse/SERVER-1243

    val winnersUpdated = setWinnerSolutions.foldLeft(true) {
      case (true, winnerSolutionId) =>
        findAndModify(
          MongoDBObject(
            "id" -> id,
            "info.battleSides" -> MongoDBObject("$elemMatch" -> MongoDBObject("solutionId" -> winnerSolutionId))
          ),
          MongoDBObject(
           "$set" -> MongoDBObject(
             "info.battleSides.$.isWinner" -> true
           )
          )
        ).isDefined
      case (false, _) =>
        false
    }

    if (winnersUpdated) {
      val queryBuilder = MongoDBObject.newBuilder

      queryBuilder +=
        ("$set" -> MongoDBObject(
          "info.status" -> newStatus.toString,
          "lastModDate" -> new Date()))

      findAndModify(
        id,
        queryBuilder.result())
    } else {
      None
    }
  }

  /**
   * @inheritdoc
   */
  def updatePoints(
    id: String,
    solutionId: String,
    randomPointsChange: Int,
    friendsPointsChange: Int): Option[Battle] = {

    findAndModify(
      MongoDBObject(
        "id" -> id,
        "info.battleSides" -> MongoDBObject("$elemMatch" -> MongoDBObject("solutionId" -> solutionId))
      ),
      MongoDBObject(
        "$inc" -> MongoDBObject(
          "info.battleSides.$.pointsRandom" -> randomPointsChange,
          "info.battleSides.$.pointsFriends" -> friendsPointsChange),
        "$set" -> MongoDBObject(
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

}

