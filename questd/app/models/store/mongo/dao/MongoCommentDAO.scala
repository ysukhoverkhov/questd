package models.store.mongo.dao

import com.mongodb.casbah.commons.MongoDBObject
import models.domain.comment.Comment
import models.store.dao._
import models.store.mongo.helpers._

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
    commentedObjectId: List[String] = List.empty,
    skip: Int = 0
    ): Iterator[Comment] = {

    val queryBuilder = MongoDBObject.newBuilder

    if (commentedObjectId.nonEmpty) {
      queryBuilder += ("info.commentedObjectId" -> MongoDBObject("$in" -> commentedObjectId))
    }

    findByExample(
      example = queryBuilder.result(),
      sort = MongoDBObject(
        "info.postingDate" -> -1),
      skip = skip)
  }
}

