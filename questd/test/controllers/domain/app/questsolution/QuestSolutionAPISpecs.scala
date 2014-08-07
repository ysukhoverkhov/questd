package controllers.domain.app.questsolution

import org.specs2.mutable._
import org.specs2.runner._
import org.mockito.Mockito._

import controllers.domain._
import controllers.domain.app.user._
import models.store._
import models.domain._
import models.store.mongo._
import controllers.domain.app.quest._
import logic.UserLogic
import logic.QuestSolutionLogic
import java.util.Date
import org.mockito.Matchers

class QuestSolutionAPISpecs extends BaseAPISpecs {

  def createSolutionInfoContent = {
    QuestSolutionInfoContent(ContentReference(ContentType.Photo, "", ""), None)
  }

  def createSolution(
    solutionId: String,
    userId: String,
    questId: String,
    status: QuestSolutionStatus.Value = QuestSolutionStatus.OnVoting,
    questLevel: Int = 1,
    themeId: String = "tid",
    points: Int = 0) = {

    QuestSolution(
      id = solutionId,
      userId = userId,
      questLevel = questLevel,
      info = QuestSolutionInfo(
        content = createSolutionInfoContent,
        vip = true,
        themeId = themeId,
        questId = questId),
      status = status,
      rating = QuestSolutionRating(
        pointsRandom = points),
      voteEndDate = new Date((new Date).getTime() + 100000))
  }

  def createQuest(id: String) = {
    Quest(
      id = id,
      authorUserId = "aid",
      approveReward = Assets(1, 2, 3),
      info = QuestInfo(
        themeId = "tid",
        vip = false,
        content = QuestInfoContent(
          media = ContentReference(
            contentType = ContentType.Photo,
            storage = "la",
            reference = "tu"),
          icon = None,
          description = "desc")))
  }

  "Quest solution API" should {

    "updateQuestSolutionState calls rewardQuestSolutionAuthor if solution state is changed" in context {

      val q = createQuest(id = "qid")
      val user1 = User(id = "uid")
      val sol = createSolution("sid", user1.id, q.id)

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


