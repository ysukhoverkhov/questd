package controllers.domain.app.user

import controllers.domain.{BaseAPISpecs, OkApiResult}
import models.domain._
import org.mockito.{ArgumentMatcher, Matchers}
import testhelpers.domainstubs._

class DailyResultAPISpecs extends BaseAPISpecs {

  "DailyResult API" should {

    "Add questsIncome for each of my quests on shiftDailyResult" in context {
      val u = createUserStub()
      val q = createQuestStub(authorId = u.id, status = QuestStatus.InRotation)

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

      case class QuestIncomeMatcher(private val incomes: List[QuestsIncome]) extends ArgumentMatcher[DailyResult] {
        def matches(result: java.lang.Object): Boolean = {
          result.asInstanceOf[DailyResult].questsIncome == incomes
        }
      }

      there was one(user).addPrivateDailyResult(
        Matchers.eq(u.id),
        Matchers.argThat(QuestIncomeMatcher(List(QuestsIncome(
          questId = q.id,
          passiveIncome = Assets(50),
          timesLiked = 1,
          likesIncome = Assets(1,1,1))))))

      result must beEqualTo(OkApiResult(ShiftDailyResultResult(user = u)))
    }
  }
}

