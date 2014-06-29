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
          takenQuest = Some(QuestInfoWithID("quest_id", QuestInfo(themeId = "theme_id", vip = false, content = QuestInfoContent(ContentReference(ContentType.Photo, "", ""), None, "")))),
          questDeadline = new Date(Long.MaxValue)),
        publicProfile = PublicProfile(vip = vip),
        rights = Rights.full))
  }

  
  def createSolution = {
    QuestSolutionInfoContent(ContentReference(ContentType.Photo, "", ""), None)
  }

  "Solve Quest API" should {

    "Create regular solution for regular users" in context {

      val u = createUser(false)
      val s = createSolution

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
      val s = createSolution

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
  }

}