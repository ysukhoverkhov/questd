package controllers.domain.app.quest

import controllers.domain._
import controllers.domain.app.protocol.ProfileModificationResult
import controllers.domain.app.user.{BookmarkQuestCode, BookmarkQuestResult, BookmarkQuestRequest}
import models.domain.common.ContentVote
import models.domain.user.User
import org.mockito.Matchers.{eq => mEq}
import testhelpers.domainstubs._

class QuestAPISpecs extends BaseAPISpecs {

  "Quest API" should {

    "Decease quest points if it was selected to time line" in context {
      val q = createQuestStub()

      quest.updatePoints(
        id = mEq(q.id),
        timelinePointsChange = mEq(-1),
        likesChange = any,
        votersCountChange = any,
        cheatingChange = any,
        spamChange = any,
        pornChange = any) returns Some(q)

      val result = api.selectQuestToTimeLine(SelectQuestToTimeLineRequest(q))

      result must beEqualTo(OkApiResult(SelectQuestToTimeLineResult()))

      there was one(quest).updatePoints(
        id = mEq(q.id),
        timelinePointsChange = mEq(-1),
        likesChange = any,
        votersCountChange = any,
        cheatingChange = any,
        spamChange = any,
        pornChange = any)
    }

    "Increase likes if quest is liked" in context {
      val q = createQuestStub()

      quest.updatePoints(
        id = mEq(q.id),
        timelinePointsChange = mEq(1),
        likesChange = mEq(1),
        votersCountChange = any,
        cheatingChange = any,
        spamChange = any,
        pornChange = any) returns Some(q)

      val result = api.voteQuest(VoteQuestRequest(q, ContentVote.Cool))

      result must beAnInstanceOf[OkApiResult[VoteQuestResult]]

      there was one(quest).updatePoints(
        id = mEq(q.id),
        timelinePointsChange = mEq(1),
        likesChange = mEq(1),
        votersCountChange = any,
        cheatingChange = any,
        spamChange = any,
        pornChange = any)
    }

    "setQuestBookmark does it" in context {
      val q = createQuestStub(id = "qid")
      val u = User(id = "uid")

      quest.readById(q.id) returns Some(q)
      user.setQuestBookmark(any, any) returns Some(u)

      val result = api.bookmarkQuest(BookmarkQuestRequest(u, q.id))

      result must beEqualTo(OkApiResult(BookmarkQuestResult(BookmarkQuestCode.OK, Some(u.profile))))
      there was one(quest).readById(q.id)
      there was one(user).setQuestBookmark(any, any)
    }
  }
}


