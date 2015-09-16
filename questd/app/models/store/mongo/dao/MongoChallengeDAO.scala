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
}

