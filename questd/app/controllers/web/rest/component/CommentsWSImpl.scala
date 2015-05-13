package controllers.web.rest.component

import controllers.domain.app.user.{PostCommentResult, PostCommentRequest}
import controllers.web.helpers._

private object CommentsWSImpl {

  case class WSPostCommentRequest(
    /// id of object we comment.
    commentedObjectId: String,

    /// Optional id of comment we are responding on.
    respondedCommentId: Option[String] = None,

    /// Message itself.
    message: String
    )
  type WSPostCommentResult = PostCommentResult
}

trait CommentsWSImpl extends QuestController with SecurityWSImpl { this: WSComponent#WS =>

  import CommentsWSImpl._

  def postComment = wrapJsonApiCallReturnBody[WSPostCommentResult] { (js, r) =>
    val v = Json.read[WSPostCommentRequest](js.toString)

    api.postComment(PostCommentRequest(r.user, v.commentedObjectId, v.respondedCommentId, v.message))
  }

}

