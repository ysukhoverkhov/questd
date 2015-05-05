package controllers.domain.app.solution

import controllers.domain._
import controllers.domain.app.protocol.ProfileModificationResult
import controllers.domain.app.user._
import logic.SolutionLogic
import models.domain._
import org.mockito.Matchers.{eq => mEq}
import org.mockito.Mockito._
import testhelpers.domainstubs._

class QuestSolutionAPISpecs extends BaseAPISpecs {

  "Quest solution API" should {

    "updateQuestSolutionState calls rewardQuestSolutionAuthor if solution state is changed" in context {
      val q = createQuestStub(id = "qid")
      val user1 = User(id = "uid")
      val sol = createSolutionStub(id = "sid", authorId = user1.id, questId = q.id)

      val spiedQuestSolutionLogic = spy(new SolutionLogic(sol, api.api))
      when(api.solution2Logic(sol)).thenReturn(spiedQuestSolutionLogic)

      when(spiedQuestSolutionLogic.shouldBanCheating).thenReturn(true)
      when(spiedQuestSolutionLogic.shouldBanIAC).thenReturn(false)
      solution.updateStatus(any, any, any) returns Some(sol.copy(status = SolutionStatus.CheatingBanned))
      user.readById(user1.id) returns Some(user1)
      user.addPrivateDailyResult(any, any) returns Some(user1)
      user.storeSolutionInDailyResult(any, any) returns Some(user1)
      user.removeEntryFromTimeLineByObjectId(mEq(user1.id), mEq(sol.id)) returns Some(user1)

      quest.readById(q.id) returns Some(q)

      quest.allWithParams(
        status = List(QuestStatus.InRotation),
        authorIds = List(user1.id),
        skip = 0) returns List.empty.iterator

      val result = api.updateSolutionState(UpdateSolutionStateRequest(sol))

      there was one(quest).allWithParams(
        status = List(QuestStatus.InRotation),
        authorIds = List(user1.id),
        skip = 0)

      there was one(solution).updateStatus(mEq(sol.id), mEq(SolutionStatus.CheatingBanned), any)
      there was one(user).readById(user1.id)
      there was one(api).rewardSolutionAuthor(RewardSolutionAuthorRequest(sol.copy(status = SolutionStatus.CheatingBanned), user1))

      result must beEqualTo(OkApiResult(UpdateSolutionStateResult()))
    }

    "Cheated solution creates correct daily result" in context {
      val q = createQuestStub(id = "qid")
      val user1 = User(id = "uid")
      val sol = createSolutionStub(id = "sid", authorId = user1.id, questId = q.id)

      val spiedQuestSolutionLogic = spy(new SolutionLogic(sol, api.api))
      when(api.solution2Logic(sol)).thenReturn(spiedQuestSolutionLogic)

      when(spiedQuestSolutionLogic.shouldBanCheating).thenReturn(true)
      when(spiedQuestSolutionLogic.shouldBanIAC).thenReturn(false)
      solution.updateStatus(any, any, any) returns Some(sol.copy(status = SolutionStatus.CheatingBanned))
      user.readById(user1.id) returns Some(user1)
      user.addPrivateDailyResult(any, any) returns Some(user1)
      user.storeSolutionInDailyResult(any, any) returns Some(user1)
      user.removeEntryFromTimeLineByObjectId(mEq(user1.id), mEq(sol.id)) returns Some(user1)

      quest.readById(q.id) returns Some(q)

      quest.allWithParams(
        status = List(QuestStatus.InRotation),
        authorIds = List(user1.id),
        skip = 0) returns List.empty.iterator

      val result = api.updateSolutionState(UpdateSolutionStateRequest(sol))

      there was one(quest).allWithParams(
        status = List(QuestStatus.InRotation),
        authorIds = List(user1.id),
        skip = 0)

      there was one(solution).updateStatus(mEq(sol.id), mEq(SolutionStatus.CheatingBanned), any)
      there was one(user).readById(user1.id)
      there was one(api).rewardSolutionAuthor(RewardSolutionAuthorRequest(sol.copy(status = SolutionStatus.CheatingBanned), user1))
      there was one(user).storeSolutionInDailyResult(mEq(user1.id), mEq(SolutionResult(
        solutionId = sol.id,
        battleId = None,
        reward = None,
        penalty = Some(Assets(rating = 4510)),
        status = SolutionStatus.CheatingBanned)))
      there was one(user).removeEntryFromTimeLineByObjectId(mEq(user1.id), mEq(sol.id))

      result must beEqualTo(OkApiResult(UpdateSolutionStateResult()))
    }

    "setQuestBookmark does it" in context {
      val q = createQuestStub(id = "qid")
      val u = User(id = "uid")

      quest.readById(q.id) returns Some(q)
      user.setQuestBookmark(any, any) returns Some(u)

      val result = api.bookmarkQuest(BookmarkQuestRequest(u, q.id))

      result must beEqualTo(OkApiResult(BookmarkQuestResult(ProfileModificationResult.OK, Some(u.profile))))
      there was one(quest).readById(q.id)
      there was one(user).setQuestBookmark(any, any)
    }
  }
}

