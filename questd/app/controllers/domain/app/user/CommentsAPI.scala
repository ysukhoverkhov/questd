package controllers.domain.app.user

import java.util.Date

import components._
import controllers.domain._
import controllers.domain.app.protocol.ProfileModificationResult._
import controllers.domain.helpers._
import models.domain.comment.{Comment, CommentInfo}
import models.domain.user.{Profile, User}
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
  objectId: String,
  pageNumber: Int,
  pageSize: Int)
case class GetCommentsForObjectResult(
  allowed: ProfileModificationResult,
  comments: List[CommentView],
  pageSize: Int,
  hasMore: Boolean)


private[domain] trait CommentsAPI { this: DomainAPIComponent#DomainAPI with DBAccessor =>

  /**
   * Post a comment to an object.
   */
  def postComment(request: PostCommentRequest): ApiResult[PostCommentResult] = handleDbException {

    lazy val charLimitExceeded = request.message.length > config(api.ConfigParams.CommentsMaxLength).toInt

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

      OkApiResult(PostCommentResult(OK, Some(request.user.profile)))
    }
  }


  /**
   * Get comments for objects.
   */// TODO: test me.
  def getCommentsForObject(request: GetCommentsForObjectRequest): ApiResult[GetCommentsForObjectResult] = handleDbException {
    val pageSize = adjustedPageSize(request.pageSize)
    val pageNumber = adjustedPageNumber(request.pageNumber)

    val commentsForObject = db.comment.allWithParams(
      objectIds = List(request.objectId),
      skip = pageNumber * pageSize)

    val comments = commentsForObject.take(pageSize).toList.map(c => {
      CommentView(c.id, c.info)
    })

    OkApiResult(GetCommentsForObjectResult(
      allowed = OK,
      comments = comments,
      pageSize,
      commentsForObject.hasNext))
  }

}

