package controllers.domain.app.comment

import java.util.Date

import components._
import controllers.domain._
import controllers.domain.app.protocol.CommonCode
import controllers.domain.app.user.MakeTaskRequest
import controllers.domain.helpers._
import models.domain.comment.{Comment, CommentInfo}
import models.domain.user.User
import models.domain.user.profile.{Profile, TaskType}
import models.view.CommentView

object PostCommentCode extends Enumeration with CommonCode {
  val CommentLengthLimitExceeded = Value
  val CommentToRespondNotFound = Value
  val ObjectNotFound = Value
}
case class PostCommentRequest(
  user: User,
  commentedObjectId: String,
  respondedCommentId: Option[String],
  message: String)
case class PostCommentResult(
  allowed: PostCommentCode.Value,
  profile: Option[Profile] = None)


object GetCommentsForObjectCode extends Enumeration with CommonCode {

}
case class GetCommentsForObjectRequest(
  user: User,
  commentedObjectId: String,
  pageNumber: Int,
  pageSize: Int,
  untilCommentId: Option[String])
case class GetCommentsForObjectResult(
  allowed: GetCommentsForObjectCode.Value,
  comments: List[CommentView],
  hasMore: Boolean)


private[domain] trait CommentsAPI { this: DomainAPIComponent#DomainAPI with DBAccessor =>

  /**
   * Post a comment to an object.
   */
  def postComment(request: PostCommentRequest): ApiResult[PostCommentResult] = handleDbException {

    import PostCommentCode._

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
      OkApiResult(PostCommentResult(CommentLengthLimitExceeded))
    else if (respondNotFound)
      OkApiResult(PostCommentResult(CommentToRespondNotFound))
    else if (objectNotFound)
      OkApiResult(PostCommentResult(ObjectNotFound))
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
    import GetCommentsForObjectCode._

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

