package models.store.mongo.dao

import java.util.Date

import com.mongodb.casbah.commons.MongoDBObject
import models.domain._
import models.store.dao._
import models.store.mongo.helpers._
import play.Logger

/**
 * DOA for Quest solution objects
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

    if (status.length > 0) {
      queryBuilder += ("info.status" -> MongoDBObject("$in" -> status.map(_.toString)))
    }

    if (authorIds.length > 0) {
      queryBuilder += ("info.authorIds" -> MongoDBObject("$in" -> authorIds))
    }

    if (solutionIds.length > 0) {
      queryBuilder += ("info.solutionIds" -> MongoDBObject("$in" -> solutionIds))
    }

    if (authorIdsExclude.length > 0) {
      queryBuilder += ("info.authorIds" -> MongoDBObject("$nin" -> authorIdsExclude))
    }

    if (levels != None) {
      queryBuilder += ("$and" -> Array(
        MongoDBObject("level" -> MongoDBObject("$gte" -> levels.get._1)),
        MongoDBObject("level" -> MongoDBObject("$lte" -> levels.get._2))))
    }

    if (vip != None) {
      queryBuilder += ("vip" -> vip.get)
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
    addWinners: List[String] = List.empty): Option[Battle] = {

    val queryBuilder = MongoDBObject.newBuilder

    queryBuilder +=
      ("$set" -> MongoDBObject(
        "info.status" -> newStatus.toString,
        "lastModDate" -> new Date()))

    if (addWinners.length > 0) {
      queryBuilder +=
        ("$push" -> MongoDBObject(
          "info.winnerIds" -> MongoDBObject(
            "$each" -> addWinners)))
    }

    findAndModify(
      id,
      queryBuilder.result())
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

