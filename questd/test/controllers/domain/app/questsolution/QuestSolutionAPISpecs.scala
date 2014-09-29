package controllers.domain.app.questsolution

import org.mockito.Mockito._
import org.mockito.Matchers
import controllers.domain._
import controllers.domain.app.user._
import models.domain._
import logic.QuestSolutionLogic
import testhelpers.domainstubs._

class QuestSolutionAPISpecs extends BaseAPISpecs {

  "Quest solution API" should {

    "updateQuestSolutionState calls rewardQuestSolutionAuthor if solution state is changed" in context {

      val q = createQuestStub(id = "qid")
      val user1 = User(id = "uid")
      val sol = createSolutionStub(id = "sid", userId = user1.id, questId = q.id)

      val spiedQuestSolutionLogic = spy(new QuestSolutionLogic(sol, api.api))
      when(api.questSolution2Logic(sol)).thenReturn(spiedQuestSolutionLogic)

      when(spiedQuestSolutionLogic.shouldStopVoting).thenReturn(true)
      solution.updateStatus(any, any, any) returns Some(sol.copy(status = QuestSolutionStatus.WaitingForCompetitor))
      user.readById(user1.id) returns Some(user1)
      quest.readById(q.id) returns Some(q)

      solution.allWithParams(
        status = List(QuestSolutionStatus.WaitingForCompetitor.toString),
        questIds = List(sol.info.questId)) returns List(sol).iterator

      val result = api.updateQuestSolutionState(UpdateQuestSolutionStateRequest(sol))

      result must beEqualTo(OkApiResult(UpdateQuestSolutionStateResult()))

      there was one(solution).updateStatus(Matchers.eq(sol.id), Matchers.eq(QuestSolutionStatus.WaitingForCompetitor.toString), Matchers.eq(null))
      there was one(user).readById(user1.id)
      there was one(api).rewardQuestSolutionAuthor(RewardQuestSolutionAuthorRequest(sol.copy(status = QuestSolutionStatus.WaitingForCompetitor), user1))
    }
  }
}


