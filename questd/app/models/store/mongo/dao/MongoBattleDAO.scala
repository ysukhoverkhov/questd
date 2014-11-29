package models.store.mongo.dao

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
  // TODO: levels should work.
  def allWithParams(
    status: List[BattleStatus.Value] = List(),
//    authorIds: List[String] = List(),
    levels: Option[(Int, Int)] = None,
    skip: Int = 0
//    vip: Option[Boolean] = None,
//    ids: List[String] = List(),
//    questIds: List[String] = List(),
//    themeIds: List[String] = List(),
/*    cultureId: Option[String] = None*/): Iterator[Battle] = {

    val queryBuilder = MongoDBObject.newBuilder

    if (status.length > 0) {
      queryBuilder += ("status" -> MongoDBObject("$in" -> status.map(_.toString)))
    }

//    if (authorIds.length > 0) {
//      queryBuilder += ("info.authorId" -> MongoDBObject("$in" -> authorIds))
//    }
//
//    if (levels != None) {
//      queryBuilder += ("$and" -> Array(
//        MongoDBObject("questLevel" -> MongoDBObject("$gte" -> levels.get._1)),
//        MongoDBObject("questLevel" -> MongoDBObject("$lte" -> levels.get._2))))
//    }
//
//    if (vip != None) {
//      queryBuilder += ("info.vip" -> vip.get)
//    }
//
//    if (ids.length > 0) {
//      queryBuilder += ("id" -> MongoDBObject("$in" -> ids))
//    }
//
//    if (questIds.length > 0) {
//      queryBuilder += ("info.questId" -> MongoDBObject("$in" -> questIds))
//    }
//
//    if (themeIds.length > 0) {
//      queryBuilder += ("info.themeId" -> MongoDBObject("$in" -> themeIds))
//    }
//
//    if (cultureId != None) {
//      queryBuilder += ("cultureId" -> cultureId.get)
//    }

    Logger.trace("MongoQuestSolutionDAO - allWithParams - " + queryBuilder.result)

    findByExample(
      example = queryBuilder.result(),
      skip = skip)
  }

  /**
   * @inheritdoc
   */
  def updateStatus(
    id: String,
    newStatus: BattleStatus.Value): Option[Battle] = {

    val queryBuilder = MongoDBObject.newBuilder

      queryBuilder +=
        ("$set" -> MongoDBObject(
          "status" -> newStatus.toString))

    findAndModify(
      id,
      queryBuilder.result())
  }

}

