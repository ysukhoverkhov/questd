package controllers.domain.app.user

import controllers.domain.{BaseAPISpecs, OkApiResult}
import models.domain.common.Assets
import models.domain.quest.QuestStatus
import models.domain.solution.SolutionStatus
import models.domain.user.dailyresults._
import org.mockito.Matchers.{eq => mEq}
import org.mockito.{ArgumentMatcher, Matchers}
import org.mockito.Mockito._
import testhelpers.domainstubs._

class DailyResultAPISpecs extends BaseAPISpecs {

  "DailyResult API" should {

    "Add questsIncome for each of my quests on shiftDailyResult" in context {
      val u = createUserStub()
      val q = createQuestStub(authorId = u.id, status = QuestStatus.InRotation, likes = 5)

      quest.allWithParams(
        status = mEq(List(QuestStatus.InRotation)),
        authorIds = mEq(List(u.id)),
        authorIdsExclude = any,
        levels = any,
        skip = mEq(0),
        vip = any,
        ids = any,
        idsExclude = any,
        cultureId = any,
        withSolutions = any) returns List(q).iterator
      user.addPrivateDailyResult(any, any) returns Some(u)
      user.addQuestIncomeToDailyResult(any, any) returns Some(u)

      doReturn(OkApiResult(SendMessageResult(u))).when(api).sendMessage(any)

      val result = api.shiftDailyResult(ShiftDailyResultRequest(
        user = u))

      there was one(quest).allWithParams(
        status = mEq(List(QuestStatus.InRotation)),
        authorIds = mEq(List(u.id)),
        authorIdsExclude = any,
        levels = any,
        skip = mEq(0),
        vip = any,
        ids = any,
        idsExclude = any,
        cultureId = any,
        withSolutions = any)

      there was one(user).addQuestIncomeToDailyResult(
        mEq(u.id),
        mEq(QuestIncome(questId = q.id,
          passiveIncome = Assets(50),
          timesLiked = 5,
          likesIncome = Assets(3,0,0))))

      there was one(user).addPrivateDailyResult(
        mEq(u.id),
        Matchers.argThat(new ArgumentMatcher[DailyResult] {
          def matches(result: java.lang.Object): Boolean = {
            result.asInstanceOf[DailyResult].questsIncome == List.empty
          }
        }))

      there was one(api).sendMessage(any)

      result must beEqualTo(OkApiResult(ShiftDailyResultResult(user = u)))
    }

    "Apply all income on making private daily results public " in context {
      val dr = createDailyResultStub(
        questsIncome = List(
          createQuestIncomeStub(
            passiveIncome = Assets(2, 2, 2),
            likesIncome = Assets(4, 4, 4),
            solutionsIncome = Assets(8, 8, 8)),
          createQuestIncomeStub(
            passiveIncome = Assets(16, 16, 16),
            likesIncome = Assets(32, 32, 32),
            solutionsIncome = Assets(64, 64, 64))
        ),
        questResult = List(QuestResult(
          questId = "1",
          reward = Assets(256, 256, 256),
          status = QuestStatus.InRotation)),
        solutionResult = List(SolutionResult(
          solutionId = "1",
          reward = -Assets(-128, -128, -128),
          status = SolutionStatus.CheatingBanned)),
        battleResult = List(BattleResult(
          battleId = "1",
          reward = Assets(512, 512, 512),
          isVictory = true))
      )

      val u = createUserStub(privateDailyResults = List(
        dr,
        dr
      ))

      user.addToAssets(any, any) returns Some(u)

      user.movePrivateDailyResultsToPublic(any, any) returns Some(u.copy(
        privateDailyResults = List(u.privateDailyResults.head),
        profile = u.profile.copy(
          dailyResults = u.privateDailyResults.tail
        )
      ))

      val result = api.getDailyResult(GetDailyResultRequest(u))

      there was one(user).movePrivateDailyResultsToPublic(u.id, u.privateDailyResults.tail)
      there was one(user).addToAssets(u.id, Assets(1022, 1022, 1022))
      result must beAnInstanceOf[OkApiResult[GetDailyResultResult]]
    }
  }
}

