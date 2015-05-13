package controllers.domain.app.user

import java.util.Date

import components._
import controllers.domain._
import controllers.domain.app.protocol.ProfileModificationResult._
import controllers.domain.helpers._
import models.domain.comment.{CommentInfo, Comment}
import models.domain.user.{Profile, User}

case class PostCommentRequest(
  user: User,
  commentedObjectId: String,
  respondedCommentId: Option[String],
  message: String)
case class PostCommentResult(
  allowed: ProfileModificationResult,
  profile: Option[Profile] = None)

private[domain] trait CommentsAPI { this: DBAccessor =>

  /**
   * Post a comment to an object.
   */
  def postComment(request: PostCommentRequest): ApiResult[PostCommentResult] = handleDbException {

    // TODO: get limit from config.
    val charLimitExceeded = request.message.length > 5

    val respondToExists = request.respondedCommentId.fold(true)(db.comment.readById(_).isDefined)

    val objectExists = List(
      db.quest.readById(_: String).isDefined,
      db.solution.readById(_: String).isDefined,
      db.battle.readById(_: String).isDefined
    ).foldLeft(false) {
      case (true, _) => true
      case (false, block) => block(request.commentedObjectId)
    }

    (!respondToExists || !objectExists, charLimitExceeded) match {
      case (true, _) =>
        OkApiResult(PostCommentResult(OutOfContent))
      case (_, true) =>
        OkApiResult(PostCommentResult(LimitExceeded))
      case _ =>
        db.comment.create(Comment(info = CommentInfo(
          commentedObjectId = request.commentedObjectId,
          authorId = request.user.id,
          respondedCommentId = request.respondedCommentId,
          postingDate = new Date(),
          message = request.message
        )))

        OkApiResult(PostCommentResult(OK, Some(request.user.profile)))
    }
    // TODO: test normal.
    // TODO: test limit.
    // TODO: test no parent.
    // TODO: test no content.
  }
}

