package models.domain.comment

import models.domain.base.ID

/**
 * A comment for a thing.
 */
case class Comment (
  id: String = ID.generateUUID(),
  info: CommentInfo
  ) extends ID
