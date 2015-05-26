package controllers.domain.app.quest

import controllers.domain._
import models.domain.common.ContentVote
import org.mockito.Matchers.{eq => mEq}
import testhelpers.domainstubs._

class QuestAPISpecs extends BaseAPISpecs {

  "Quest API" should {

    "Decease quest points if it was selected to time line" in context {

      val q = createQuestStub()

      quest.updatePoints(
        id = mEq(q.id),
        pointsChange = mEq(-1),
        likesChange = any,
        votersCountChange = any,
        cheatingChange = any,
        spamChange = any,
        pornChange = any) returns Some(q)

      val result = api.selectQuestToTimeLine(SelectQuestToTimeLineRequest(q))

      result must beEqualTo(OkApiResult(SelectQuestToTimeLineResult()))

      there was one(quest).updatePoints(
        id = mEq(q.id),
        pointsChange = mEq(-1),
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
        pointsChange = mEq(1),
        likesChange = mEq(1),
        votersCountChange = any,
        cheatingChange = any,
        spamChange = any,
        pornChange = any) returns Some(q)

      val result = api.voteQuest(VoteQuestRequest(q, ContentVote.Cool))

      result must beEqualTo(OkApiResult(VoteQuestResult()))

      there was one(quest).updatePoints(
        id = mEq(q.id),
        pointsChange = mEq(1),
        likesChange = mEq(1),
        votersCountChange = any,
        cheatingChange = any,
        spamChange = any,
        pornChange = any)
    }

  }
}


