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
        levels = null,
        skip = 0,
        vip = null,
        ids = null,
        cultureId = null) returns List(q).iterator
      user.addPrivateDailyResult(any, any) returns Some(u)

      val result = api.shiftDailyResult(ShiftDailyResultRequest(
        user = u))

      there was one(quest).allWithParams(
        status = List(QuestStatus.InRotation),
        authorIds = List(u.id),
        levels = null,
        skip = 0,
        vip = null,
        ids = null,
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
  }
}

