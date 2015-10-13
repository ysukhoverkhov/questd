package controllers.domain.app.comment

import java.util.Date

import components._
import controllers.domain._
import controllers.domain.app.protocol.ProfileModificationResult._
import controllers.domain.app.user.MakeTaskRequest
import controllers.domain.helpers._
import models.domain.comment.{Comment, CommentInfo}
import models.domain.user.User
import models.domain.user.profile.{Profile, TaskType}
import models.view.CommentView


case class PostCommentRequest(
  user: User,
  commentedObjectId: String,
  respondedCommentId: Option[String],
  message: String)
case class PostCommentResult(
  allowed: ProfileModificationResult,
  profile: Option[Profile] = None)


case class GetCommentsForObjectRequest(
  user: User,
  commentedObjectId: String,
  pageNumber: Int,
  pageSize: Int,
  untilCommentId: Option[String])
case class GetCommentsForObjectResult(
  allowed: ProfileModificationResult,
  comments: List[CommentView],
  hasMore: Boolean)


private[domain] trait CommentsAPI { this: DomainAPIComponent#DomainAPI with DBAccessor =>

  /**
   * Post a comment to an object.
   */
  def postComment(request: PostCommentRequest): ApiResult[PostCommentResult] = handleDbException {

    lazy val charLimitExceeded = request.message.length > config(api.DefaultConfigParams.CommentsMaxLength).toInt

    lazy val respondNotFound = !request.respondedCommentId.fold(true)(db.comment.readById(_).isDefined)

    lazy val objectNotFound = !List(
      db.quest.readById(_: String).isDefined,
      db.solution.readById(_: String).isDefined,
      db.battle.readById(_: String).isDefined
    ).foldLeft(false) {
      case (true, _) => true
      case (false, block) => block(request.commentedObjectId)
    }

    if (charLimitExceeded)
      OkApiResult(PostCommentResult(LimitExceeded))
    else if (respondNotFound || objectNotFound)
      OkApiResult(PostCommentResult(OutOfContent))
    else {
      db.comment.create(Comment(info = CommentInfo(
        commentedObjectId = request.commentedObjectId,
        authorId = request.user.id,
        respondedCommentId = request.respondedCommentId,
        postingDate = new Date(),
        message = request.message
      )))

      makeTask(MakeTaskRequest(request.user, taskType = Some(TaskType.PostComments))) map { r =>
        OkApiResult(PostCommentResult(OK, Some(r.user.profile)))
      }
    }
  }

  /**
   * Get comments for objects.
   */
  def getCommentsForObject(request: GetCommentsForObjectRequest): ApiResult[GetCommentsForObjectResult] = handleDbException {
    val pageSize = adjustedPageSize(request.pageSize)
    val pageNumber = adjustedPageNumber(request.pageNumber)

    val commentsForObject = db.comment.allWithParams(
      commentedObjectId = List(request.commentedObjectId),
      authorIdsExclude = request.user.banned,
      skip = pageNumber * pageSize)

    val comments = commentsForObject
      .take(pageSize)
      .takeWhile(c => request.untilCommentId.fold(true)(_ != c.id))
      .toList
      .map(c => {
      CommentView(c.id, c.info)
    })

    OkApiResult(GetCommentsForObjectResult(
      allowed = OK,
      comments = comments,
      commentsForObject.hasNext))
  }
}

