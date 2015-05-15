package models.store.mongo.dao

import com.mongodb.casbah.commons.MongoDBObject
import models.domain.comment.Comment
import models.store.dao._
import models.store.mongo.helpers._
import play.Logger

/**
 * DOA for Comments
 */
private[mongo] class MongoCommentDAO
  extends BaseMongoDAO[Comment](collectionName = "comments")
  with CommentDAO {

  /**
   * @inheritdoc
   */
  def allWithParams(
    objectIds: List[String] = List.empty,
    skip: Int = 0
    ): Iterator[Comment] = {

    val queryBuilder = MongoDBObject.newBuilder

    if (objectIds.nonEmpty) {
      queryBuilder += ("info.commentedObjectId" -> MongoDBObject("$in" -> objectIds))
    }

    Logger.trace("MongoCommentDAO - allWithParams - " + queryBuilder.result)

    findByExample(
      example = queryBuilder.result(),
      sort = MongoDBObject(
        "info.postingDate" -> 1),
      skip = skip)
  }
}

