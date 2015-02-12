package controllers.domain.app.user

import controllers.domain.{BaseAPISpecs, OkApiResult}
import models.domain._
import org.mockito.{ArgumentMatcher, Matchers}
import testhelpers.domainstubs._

class DailyResultAPISpecs extends BaseAPISpecs {

  "DailyResult API" should {

    "Add questsIncome for each of my quests on shiftDailyResult" in context {
      val u = createUserStub()
      val q = createQuestStub(authorId = u.id, status = QuestStatus.InRotation, likes = 5)

      quest.allWithParams(
        status = List(QuestStatus.InRotation),
        authorIds = List(u.id),
        authorIdsExclude = null,
        levels = null,
        skip = 0,
        vip = null,
        ids = null,
        idsExclude = null,
        cultureId = null) returns List(q).iterator
      user.addPrivateDailyResult(any, any) returns Some(u)

      val result = api.shiftDailyResult(ShiftDailyResultRequest(
        user = u))

      there was one(quest).allWithParams(
        status = List(QuestStatus.InRotation),
        authorIds = List(u.id),
        authorIdsExclude = null,
        levels = null,
        skip = 0,
        vip = null,
        ids = null,
        idsExclude = null,
        cultureId = null)

      case class QuestIncomeMatcher(private val incomes: List[QuestIncome]) extends ArgumentMatcher[DailyResult] {
        def matches(result: java.lang.Object): Boolean = {
          result.asInstanceOf[DailyResult].questsIncome == incomes
        }
      }

      there was one(user).addPrivateDailyResult(
        Matchers.eq(u.id),
        Matchers.argThat(new ArgumentMatcher[DailyResult] {
          def matches(result: java.lang.Object): Boolean = {
            result.asInstanceOf[DailyResult].questsIncome == List(QuestIncome(
              questId = q.id,
              passiveIncome = Assets(50),
              timesLiked = 5,
              likesIncome = Assets(3,0,0)))
          }
        }))

      result must beEqualTo(OkApiResult(ShiftDailyResultResult(user = u)))
    }

    "Apply all income on making private daily results public " in context {
      val dr = createDailyResultStub(
        dailySalary = Assets(1, 1, 1),
        questsIncome = List(
          createQuestIncomeStub(
            passiveIncome = Assets(2, 2, 2),
            likesIncome = Assets(4, 4, 4),
            solutionsIncome = Assets(8, 8, 8)),
          createQuestIncomeStub(
            passiveIncome = Assets(16, 16, 16),
            likesIncome = Assets(32, 32, 32),
            solutionsIncome = Assets(64, 64, 64))
        )
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
      there was one(user).addToAssets(u.id, Assets(127, 127, 127))
      result must beAnInstanceOf[OkApiResult[GetDailyResultResult]]
    }
  }
}

