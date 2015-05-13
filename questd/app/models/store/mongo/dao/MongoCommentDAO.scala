package models.store.mongo.dao

import models.domain.comment.Comment
import models.store.dao._
import models.store.mongo.helpers._

/**
 * DOA for Comments
 */
private[mongo] class MongoCommentDAO
  extends BaseMongoDAO[Comment](collectionName = "comments")
  with CommentDAO {

}

