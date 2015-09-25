package controllers.web.rest.component

import controllers.domain.app.comment.{GetCommentsForObjectRequest, PostCommentRequest, GetCommentsForObjectResult, PostCommentResult}
import controllers.web.helpers._

private object CommentsWSImplTypes {

  case class WSPostCommentRequest(
    /// id of object we comment.
    commentedObjectId: String,

    /// Optional id of comment we are responding on.
    respondedCommentId: Option[String] = None,

    /// Message itself.
    message: String
    )
  type WSPostCommentResult = PostCommentResult


  case class WSGetCommentsForObjectRequest(
    // Id of object o get comments for.
    commentedObjectId: String,

    // Number of page in result, zero based.
    pageNumber: Int,

    // Number of items on a page.
    pageSize: Int
    )
  type WSGetCommentsForObjectResult = GetCommentsForObjectResult
}

trait CommentsWSImpl extends QuestController with SecurityWSImpl { this: WSComponent#WS =>

  import CommentsWSImplTypes._

  /**
   * Post a single comment to arbitrary object.
   *
   * @return
   */
  def postComment = wrapJsonApiCallReturnBody[WSPostCommentResult] { (js, r) =>
    val v = Json.read[WSPostCommentRequest](js.toString)

    api.postComment(PostCommentRequest(r.user, v.commentedObjectId, v.respondedCommentId, v.message))
  }

  /**
   * Get comments for a object.
   *
   * @return
   */
  def getCommentsForObject = wrapJsonApiCallReturnBody[WSGetCommentsForObjectResult] { (js, r) =>
    val v = Json.read[WSGetCommentsForObjectRequest](js.toString)

    api.getCommentsForObject(GetCommentsForObjectRequest(r.user, v.commentedObjectId, v.pageNumber, v.pageSize))
  }
}

