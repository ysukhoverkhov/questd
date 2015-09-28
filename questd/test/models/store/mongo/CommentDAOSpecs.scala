

package models.store.mongo

import org.specs2.mutable._
import play.api.test.WithApplication
import testhelpers.domainstubs._

//noinspection ZeroIndexToHead
//@RunWith(classOf[JUnitRunner])
class CommentDAOSpecs extends BaseDAOSpecs {

  private[this] def clearDB() = {
    db.comment.clear()
  }

  "Mongo Comment DAO" should {

    "Get all comments" in new WithApplication(appWithTestDatabase) {

      clearDB()

      // Preparing quests to store in db.

      val comments = List(
        createCommentStub(
          id = "c1"),

        createCommentStub(
          id = "c2"),

        createCommentStub(
          id = "c3"))

      comments.foreach(db.comment.create)

      val all = db.comment.all.toList
      all.size must beEqualTo(comments.size)
      all.map(_.id).sorted must beEqualTo(List(comments(0).id, comments(2).id, comments(1).id).sorted)

      val allWithParams = db.comment.allWithParams().toList
      allWithParams.size must beEqualTo(comments.size)
      allWithParams.map(_.id).sorted must beEqualTo(List(comments(0).id, comments(2).id, comments(1).id).sorted)

      val objectIds = db.comment.allWithParams(commentedObjectId = List(comments(1).info.commentedObjectId)).toList
      objectIds.map(_.id) must beEqualTo(List(comments(1).id))

      val objectIdsSkip = db.comment.allWithParams(commentedObjectId = comments.map(_.info.commentedObjectId).take(2), skip = 1).toList
      objectIdsSkip.map(_.id).size must beEqualTo(1)
      objectIdsSkip.map(_.id) must beEqualTo(List(comments(1).id)) or beEqualTo(List(comments(0).id))

      val authorIdsExclude = db.comment.allWithParams(authorIdsExclude = List(comments(1).info.authorId)).toList
      authorIdsExclude.map(_.id).sorted must beEqualTo(List(comments(0).id, comments(2).id).sorted)
    }
  }
}

