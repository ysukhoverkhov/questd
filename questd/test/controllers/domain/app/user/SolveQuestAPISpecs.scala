package controllers.domain.app.user

import controllers.domain.BaseAPISpecs
import models.domain._
import models.domain.base._
import java.util.Date
import controllers.domain.app.protocol.ProfileModificationResult

class SolveQuestAPISpecs extends BaseAPISpecs {

  def createUser(vip: Boolean) = {
    User(
      id = "user_id",
      profile = Profile(
        questSolutionContext = QuestSolutionContext(
          takenQuest = Some(QuestInfoWithID("quest_id", QuestInfo(themeId = "theme_id", vip = false, content = QuestInfoContent(ContentReference(ContentType.Photo.toString(), "", ""), None, "")))),
          questDeadline = new Date(Long.MaxValue)),
        publicProfile = PublicProfile(vip = vip),
        rights = Rights.full))
  }

  def createSolutionInfoContent = {
    QuestSolutionInfoContent(ContentReference(ContentType.Photo.toString(), "", ""), None)
  }

  def createSolution(
    userId: String,
    questId: String,
    status: String = QuestSolutionStatus.OnVoting.toString,
    questLevel: Int = 1,
    themeId: String = "tid") = {

    QuestSolution(
      userId = userId,
      questLevel = questLevel,
      info = QuestSolutionInfo(
        content = createSolutionInfoContent,
        vip = true,
        themeId = themeId,
        questId = questId),
      status = status)
  }

  "Solve Quest API" should {

    "Create regular solution for regular users" in context {

      val u = createUser(false)
      val s = createSolutionInfoContent

      user.resetQuestSolution(any, any) returns Some(u)

      val result = api.proposeSolution(ProposeSolutionRequest(u, s))

      result.body.get.allowed must beEqualTo(ProfileModificationResult.OK)

      there was one(solution).create(
        QuestSolution(
          id = anyString,
          userId = u.id,
          questLevel = u.profile.questSolutionContext.takenQuest.get.obj.level,
          info = QuestSolutionInfo(
            content = s,
            themeId = u.profile.questSolutionContext.takenQuest.get.obj.themeId,
            questId = u.profile.questSolutionContext.takenQuest.get.id,
            vip = false)))
    }

    "Create VIP solution for VIP users" in context {

      val u = createUser(true)
      val s = createSolutionInfoContent

      user.resetQuestSolution(any, any) returns Some(u)

      val result = api.proposeSolution(ProposeSolutionRequest(u, s))

      result.body.get.allowed must beEqualTo(ProfileModificationResult.OK)

      there was one(solution).create(
        QuestSolution(
          id = anyString,
          userId = u.id,
          questLevel = u.profile.questSolutionContext.takenQuest.get.obj.level,
          info = QuestSolutionInfo(
            content = s,
            themeId = u.profile.questSolutionContext.takenQuest.get.obj.themeId,
            questId = u.profile.questSolutionContext.takenQuest.get.id,
            vip = true)))
    }

    "Do not fight with himself in quest" in context {

      val mySolution = createSolution("uid", "qid")

      solution.allWithParams(
        status = Some(QuestSolutionStatus.WaitingForCompetitor.toString),
        questIds = List(mySolution.info.questId)) returns List(mySolution).iterator

      api.tryFightQuest(TryFightQuestRequest(mySolution))
      // db.solution.updateStatus was not called

      success
      
    }

    "receive reward for winning quest battle" in context {
//      solution.allWithParams(any) returns List().iterator

      todo
    }
  }

}