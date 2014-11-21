package controllers.domain.app.questsolution

import org.mockito.Mockito._
import org.mockito.Matchers
import controllers.domain._
import controllers.domain.app.user._
import models.domain._
import logic.SolutionLogic
import testhelpers.domainstubs._

class QuestSolutionAPISpecs extends BaseAPISpecs {

  "Quest solution API" should {

    "updateQuestSolutionState calls rewardQuestSolutionAuthor if solution state is changed" in context {

      val q = createQuestStub(id = "qid")
      val user1 = User(id = "uid")
      val sol = createSolutionStub(id = "sid", authorId = user1.id, questId = q.id)

      val spiedQuestSolutionLogic = spy(new SolutionLogic(sol, api.api))
      when(api.solution2Logic(sol)).thenReturn(spiedQuestSolutionLogic)

//      when(spiedQuestSolutionLogic.shouldStopVoting).thenReturn(false)
      when(spiedQuestSolutionLogic.shouldBanCheating).thenReturn(true)
      when(spiedQuestSolutionLogic.shouldBanIAC).thenReturn(false)
      solution.updateStatus(any, any, any) returns Some(sol.copy(status = SolutionStatus.CheatingBanned))
      user.readById(user1.id) returns Some(user1)
      user.addPrivateDailyResult(any, any) returns Some(user1)
      user.storeSolutionInDailyResult(any, any) returns Some(user1)

      quest.readById(q.id) returns Some(q)

      quest.allWithParams(
        status = List(QuestStatus.InRotation),
        authorIds = List(user1.id),
        skip = 0) returns List().iterator

      val result = api.updateQuestSolutionState(UpdateSolutionStateRequest(sol))

      there was two(quest).allWithParams(
        status = List(QuestStatus.InRotation),
        authorIds = List(user1.id),
        skip = 0)

      there was one(solution).updateStatus(Matchers.eq(sol.id), Matchers.eq(SolutionStatus.CheatingBanned), any)
      there was one(user).readById(user1.id)
      there was one(api).rewardSolutionAuthor(RewardSolutionAuthorRequest(sol.copy(status = SolutionStatus.CheatingBanned), user1))

      result must beEqualTo(OkApiResult(UpdateSolutionStateResult()))
    }
  }
}

