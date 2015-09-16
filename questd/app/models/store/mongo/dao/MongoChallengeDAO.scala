package models.store.mongo.dao

import models.domain.challenge.Challenge
import models.store.dao._
import models.store.mongo.helpers._

/**
 * DOA for Comments
 */
private[mongo] class MongoChallengeDAO
  extends BaseMongoDAO[Challenge](collectionName = "challenges")
  with ChallengeDAO {

  /**
   * @inheritdoc
   */
//  def allWithParams(
//    commentedObjectId: List[String] = List.empty,
//    skip: Int = 0
//    ): Iterator[Comment] = {
//
//    val queryBuilder = MongoDBObject.newBuilder
//
//    if (commentedObjectId.nonEmpty) {
//      queryBuilder += ("info.commentedObjectId" -> MongoDBObject("$in" -> commentedObjectId))
//    }
//
//    findByExample(
//      example = queryBuilder.result(),
//      sort = MongoDBObject(
//        "info.postingDate" -> -1),
//      skip = skip)
//  }


//  /**
//   * @inheritdoc
//   */
//  def addBattleRequest(id: String, battleRequest: Challenge): Option[User] = {
//    findAndModify(
//      id,
//      MongoDBObject(
//        "$push" -> MongoDBObject(
//          "battleRequests" -> grater[Challenge].asDBObject(battleRequest))))
//  }
//
//  /**
//   * @inheritdoc
//   */
//  def updateBattleRequest(id: String, mySolutionId: String, opponentSolutionId: String, status: String): Option[User] = {
//    findAndModify(
//      MongoDBObject(
//        "id" -> id,
//        "battleRequests" -> MongoDBObject(
//          "$elemMatch" -> MongoDBObject(
//            "mySolutionId" -> mySolutionId,
//            "opponentSolutionId" -> opponentSolutionId))),
//      MongoDBObject(
//        "$set" -> MongoDBObject(
//          "battleRequests.$.status" -> status)))
//  }

}

