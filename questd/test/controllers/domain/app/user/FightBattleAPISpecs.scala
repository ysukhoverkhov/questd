package controllers.domain.app.user

import controllers.domain.{OkApiResult, BaseAPISpecs}
import models.domain.solution.SolutionStatus
import org.mockito.Matchers.{eq => mEq}
import testhelpers.domainstubs._

//noinspection ZeroIndexToHead
class FightBattleAPISpecs extends BaseAPISpecs {

  "FightBattle API" should {

    "Do not create battle if rival not found" in context {
      val s = createSolutionStub()

      solution.allWithParams(
        status = mEq(List(SolutionStatus.InRotation)),
        authorIds = any,
        authorIdsExclude = any,
        levels = any,
        skip = any,
        vip = any,
        ids = any,
        idsExclude = any,
        questIds = mEq(List(s.info.questId)),
        themeIds = any,
        cultureId = mEq(Some(s.cultureId))) returns List(s).iterator

      val result = api.tryCreateBattle(TryCreateBattleRequest(s))

      there was one(solution).allWithParams(
        status = mEq(List(SolutionStatus.InRotation)),
        authorIds = any,
        authorIdsExclude = any,
        levels = any,
        skip = any,
        vip = any,
        ids = any,
        idsExclude = any,
        questIds = mEq(List(s.info.questId)),
        themeIds = any,
        cultureId = mEq(Some(s.cultureId)))

      there was no(battle).create(any)
      there was no(solution).update(any)

      result must beAnInstanceOf[OkApiResult[TryCreateBattleRequest]]
    }

    // TODO: uncomment me.
//    "Create battle if rival found" in context {
//      val ss = List(
//        createSolutionStub(
//          status = SolutionStatus.InRotation,
//          authorId = "aid1"),
//        createSolutionStub(
//          status = SolutionStatus.InRotation,
//          authorId = "aid2"))
//
//      solution.allWithParams(
//        status = mEq(List(SolutionStatus.InRotation)),
//        authorIds = any,
//        authorIdsExclude = any,
//        levels = any,
//        skip = any,
//        vip = any,
//        ids = any,
//        idsExclude = any,
//        questIds = mEq(List(ss(0).info.questId)),
//        themeIds = any,
//        cultureId = mEq(Some(ss(0).cultureId))) returns ss.iterator
//      user.readById(any) returns Some(createUserStub())
//      user.addEntryToTimeLine(any, any) returns Some(createUserStub())
//
//      val result = api.tryCreateBattle(TryCreateBattleRequest(ss(0)))
//
//      there was one(solution).allWithParams(
//        status = mEq(List(SolutionStatus.InRotation)),
//        authorIds = any,
//        authorIdsExclude = any,
//        levels = any,
//        skip = any,
//        vip = any,
//        ids = any,
//        idsExclude = any,
//        questIds = mEq(List(ss(0).info.questId)),
//        themeIds = any,
//        cultureId = mEq(Some(ss(0).cultureId)))
//
//      there was one(battle).create(any)
//      there was two(solution).updateStatus(any, any, any)
//      there were two(user).addEntryToTimeLine(any, any)
//      there were two(user).addEntryToTimeLineMulti(any, any)
//      result must beAnInstanceOf[OkApiResult[TryCreateBattleRequest]]
//    }

    "Do not fight with himself in quest" in context {
      val ss = List(
        createSolutionStub(
          status = SolutionStatus.InRotation,
          authorId = "aid1"),
        createSolutionStub(
          status = SolutionStatus.InRotation,
          authorId = "aid1"))

      solution.allWithParams(
        status = mEq(List(SolutionStatus.InRotation)),
        authorIds = any,
        authorIdsExclude = any,
        levels = any,
        skip = any,
        vip = any,
        ids = any,
        idsExclude = any,
        questIds = mEq(List(ss(0).info.questId)),
        themeIds = any,
        cultureId = mEq(Some(ss(0).cultureId))) returns ss.iterator

      val result = api.tryCreateBattle(TryCreateBattleRequest(ss(0)))

      there was one(solution).allWithParams(
        status = mEq(List(SolutionStatus.InRotation)),
        authorIds = any,
        authorIdsExclude = any,
        levels = any,
        skip = any,
        vip = any,
        ids = any,
        idsExclude = any,
        questIds = mEq(List(ss(0).info.questId)),
        themeIds = any,
        cultureId = mEq(Some(ss(0).cultureId)))

      there was no(battle).create(any)
      there was no(solution).updateStatus(any, any, any)

      result must beAnInstanceOf[OkApiResult[TryCreateBattleRequest]]
    }

  }
}

