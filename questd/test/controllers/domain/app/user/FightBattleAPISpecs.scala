package controllers.domain.app.user

import controllers.domain.{OkApiResult, BaseAPISpecs}
import models.domain.SolutionStatus
import org.mockito.Matchers
import testhelpers.domainstubs._

class FightBattleAPISpecs extends BaseAPISpecs {

  "FightBattle API" should {

    "Do not create battle if rival not found" in context {
      val s = createSolutionStub()

      solution.allWithParams(
        status = Matchers.eq(List(SolutionStatus.WaitingForCompetitor)),
        authorIds = any,
        levels = any,
        skip = any,
        vip = any,
        ids = any,
        questIds = Matchers.eq(List(s.info.questId)),
        themeIds = any,
        cultureId = Matchers.eq(Some(s.cultureId))) returns List(s).iterator

      val result = api.tryCreateBattle(TryCreateBattleRequest(s))

      there was one(solution).allWithParams(
        status = Matchers.eq(List(SolutionStatus.WaitingForCompetitor)),
        authorIds = any,
        levels = any,
        skip = any,
        vip = any,
        ids = any,
        questIds = Matchers.eq(List(s.info.questId)),
        themeIds = any,
        cultureId = Matchers.eq(Some(s.cultureId)))

      there was no(battle).create(any)
      there was no(solution).update(any)

      result must beAnInstanceOf[OkApiResult[TryCreateBattleRequest]]
    }

    "Create battle if rival found" in context {
      val ss = List(
        createSolutionStub(
          status = SolutionStatus.WaitingForCompetitor,
          authorId = "aid1"),
        createSolutionStub(
          status = SolutionStatus.WaitingForCompetitor,
          authorId = "aid2"))

      solution.allWithParams(
        status = Matchers.eq(List(SolutionStatus.WaitingForCompetitor)),
        authorIds = any,
        levels = any,
        skip = any,
        vip = any,
        ids = any,
        questIds = Matchers.eq(List(ss(0).info.questId)),
        themeIds = any,
        cultureId = Matchers.eq(Some(ss(0).cultureId))) returns ss.iterator

      val result = api.tryCreateBattle(TryCreateBattleRequest(ss(0)))

      there was one(solution).allWithParams(
        status = Matchers.eq(List(SolutionStatus.WaitingForCompetitor)),
        authorIds = any,
        levels = any,
        skip = any,
        vip = any,
        ids = any,
        questIds = Matchers.eq(List(ss(0).info.questId)),
        themeIds = any,
        cultureId = Matchers.eq(Some(ss(0).cultureId)))

      there was one(battle).create(any)
      there was two(solution).updateStatus(any, any, any)

      result must beAnInstanceOf[OkApiResult[TryCreateBattleRequest]]
    }

    // TODO: clean me up.
    //    "Do not fight with himself in quest" in context {
    //
    //      val user1 = createUserStub(id = "user1")
    //      val mySolution = createSolutionStub(id = "solId1", authorId = user1.id, questId = "qid")
    //
    //      solution.allWithParams(
    //        status = List(SolutionStatus.WaitingForCompetitor),
    //        questIds = List(mySolution.info.questId)) returns List(mySolution).iterator
    //
    //      val result = api.tryFightQuest(TryFightQuestRequest(mySolution))
    //
    //      result must beEqualTo(OkApiResult(TryFightQuestResult()))
    //
    //      there were no(solution).updateStatus(any, any, any)
    //      there were no(user).storeSolutionInDailyResult(any, any)
    //    }

  }
}

