package controllers.domain.app.comment

import controllers.domain._
import controllers.domain.app.protocol.ProfileModificationResult
import controllers.domain.app.user.{GetCommentsForObjectResult, GetCommentsForObjectRequest, PostCommentRequest, PostCommentResult}
import models.view.CommentView
import testhelpers.domainstubs._

//noinspection ZeroIndexToHead
class CommentAPISpecs extends BaseAPISpecs {

  "Comments API" should {

    "Correctly formed comment is created" in context {
      val c = createCommentStub()
      val u = createUserStub()

      comment.readById(c.info.respondedCommentId.get) returns Some(c)
      quest.readById(c.info.commentedObjectId) returns Some(createQuestStub())

      val result = api.postComment(PostCommentRequest(
        user = u,
        commentedObjectId = c.info.commentedObjectId,
        respondedCommentId = c.info.respondedCommentId,
        message = c.info.message))

      there was one(comment).readById(any)
      there was one(quest).readById(any)

      result must beEqualTo(OkApiResult(PostCommentResult(ProfileModificationResult.OK, Some(u.profile))))
    }

    "Long messages not allowed" in context {
      val c = createCommentStub(message = (1 to 1000).mkString)
      val u = createUserStub()

      comment.readById(c.info.respondedCommentId.get) returns Some(c)
      quest.readById(c.info.commentedObjectId) returns Some(createQuestStub())

      val result = api.postComment(PostCommentRequest(
        user = u,
        commentedObjectId = c.info.commentedObjectId,
        respondedCommentId = c.info.respondedCommentId,
        message = c.info.message))

      there was no(comment).readById(any)
      there was no(quest).readById(any)

      result must beEqualTo(OkApiResult(PostCommentResult(ProfileModificationResult.LimitExceeded, None)))
    }

    "Respond to should be correct" in context {
      val c = createCommentStub()
      val u = createUserStub()

      comment.readById(c.info.respondedCommentId.get) returns None
      quest.readById(c.info.commentedObjectId) returns Some(createQuestStub())

      val result = api.postComment(PostCommentRequest(
        user = u,
        commentedObjectId = c.info.commentedObjectId,
        respondedCommentId = c.info.respondedCommentId,
        message = c.info.message))

      there was one(comment).readById(any)
      there was no(quest).readById(any)

      result must beEqualTo(OkApiResult(PostCommentResult(ProfileModificationResult.OutOfContent, None)))
    }

    "Content should be correct" in context {
      val c = createCommentStub()
      val u = createUserStub()

      comment.readById(c.info.respondedCommentId.get) returns  Some(c)
      quest.readById(c.info.commentedObjectId) returns None
      solution.readById(c.info.commentedObjectId) returns None
      battle.readById(c.info.commentedObjectId) returns None

      val result = api.postComment(PostCommentRequest(
        user = u,
        commentedObjectId = c.info.commentedObjectId,
        respondedCommentId = c.info.respondedCommentId,
        message = c.info.message))

      there was one(comment).readById(any)
      there was one(quest).readById(any)
      there was one(solution).readById(any)
      there was one(battle).readById(any)

      result must beEqualTo(OkApiResult(PostCommentResult(ProfileModificationResult.OutOfContent, None)))
    }

    "Comments fetching works" in context {
      val cs = List(createCommentStub())
      val u = createUserStub()

      comment.allWithParams(any, any) returns cs.iterator

      val result = api.getCommentsForObject(GetCommentsForObjectRequest(
        user = u,
        commentedObjectId = cs.head.info.commentedObjectId,
        pageNumber = 0,
        pageSize = 10))

      there was one(comment).allWithParams(any, any)

      result must beEqualTo(OkApiResult(GetCommentsForObjectResult(
        allowed = ProfileModificationResult.OK,
        comments = cs.map(c => CommentView(c.id, c.info)),
        pageSize = 10,
        hasMore = false)))
    }
  }
}

