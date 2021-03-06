package models.store.dao

import models.domain.comment.Comment

trait CommentDAO extends BaseDAO[Comment] {

  /**
   * Get all comments with ids.
   *
   * @param commentedObjectId Ids of objects to get comments for.
   * @param skip How many comments to skip.
   * @return Iterator with fetched comments.
   */
  def allWithParams(
    commentedObjectId: List[String] = List.empty,
    skip: Int = 0
  ): Iterator[Comment]
}

