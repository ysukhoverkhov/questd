package controllers.domain.app.user

import java.util.Date

import controllers.domain._
import controllers.domain.app.protocol.ProfileModificationResult
import models.domain._
import testhelpers.domainstubs._

class CreateQuestAPISpecs extends BaseAPISpecs {

  "Create Quest API" should {

    "Create regular quests for regular users" in context {
      val u = createUserStub(vip = false, questCreationCoolDown = new Date(0))
      val q = createQuestStub()

      user.updateQuestCreationCoolDown(any, any) returns Some(u)
      user.addEntryToTimeLine(any, any) returns Some(u)
      user.addQuestIncomeToDailyResult(any, any) returns Some(u)

      val result = api.createQuest(CreateQuestRequest(u, q.info.content))

      result.body must beSome[CreateQuestResult].which(r => r.allowed == ProfileModificationResult.OK)

      there was one(user).addEntryToTimeLine(any, any)
      there was one(user).addEntryToTimeLineMulti(any, any)
      there was one(user).addQuestIncomeToDailyResult(any, any)
      there was one(quest).create(
        Quest(
          id = anyString,
          cultureId = "cultureId",
          info = QuestInfo(
            authorId = u.id,
            level = 10,
            content = q.info.content,
            vip = false,
            solveCost = Assets(),
            solveRewardWon = Assets(),
            solveRewardLost = Assets())))
    }

    "Create VIP quests for VIP users" in context {
      val u = createUserStub(vip = true, questCreationCoolDown = new Date(0))
      val q = createQuestStub()

      user.updateQuestCreationCoolDown(any, any) returns Some(u)
      user.addEntryToTimeLine(any, any) returns Some(u)
      user.addQuestIncomeToDailyResult(any, any) returns Some(u)

      val result = api.createQuest(CreateQuestRequest(u, q.info.content))

      result.body must beSome[CreateQuestResult].which(r => r.allowed == ProfileModificationResult.OK)

      there was one(user).addEntryToTimeLine(any, any)
      there was one(user).addEntryToTimeLineMulti(any, any)
      there was one(user).addQuestIncomeToDailyResult(any, any)
      there was one(quest).create(
        Quest(
          id = anyString,
          cultureId = "cultureId",
          info = QuestInfo(
            authorId = u.id,
            level = 10,
            content = q.info.content,
            vip = true,
            solveCost = Assets(),
            solveRewardWon = Assets(),
            solveRewardLost = Assets())))
    }

    "rewardQuestAuthor removes quest from daily income if it's banned" in context {
      val u = createUserStub()
      val q = createQuestStub(status = QuestStatus.CheatingBanned)

      user.addToAssets(any, any) returns Some(u)
      user.storeProposalInDailyResult(any, any) returns Some(u)
      user.removeQuestIncomeFromDailyResult(any, any) returns Some(u)

      val result = api.rewardQuestAuthor(RewardQuestAuthorRequest(q, u))

      there was one(user).removeQuestIncomeFromDailyResult(u.id, q.id)
      result must beAnInstanceOf[OkApiResult[RewardQuestAuthorResult]]
    }
  }
}

