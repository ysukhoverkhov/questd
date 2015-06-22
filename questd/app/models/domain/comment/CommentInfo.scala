package models.domain.comment

import java.util.Date

/**
 * Public info for comment.
 */
case class CommentInfo (
  commentedObjectId: String,
  authorId: String,
  respondedCommentId: Option[String],
  postingDate: Date,
  message: String
)
