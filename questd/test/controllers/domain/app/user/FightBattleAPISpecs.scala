package controllers.domain.app.user

import controllers.domain.{OkApiResult, BaseAPISpecs}
import models.domain.battle.BattleStatus
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

    "Create battle if rival found" in context {
      val ss = List(
        createSolutionStub(
          status = SolutionStatus.InRotation,
          authorId = "aid1"),
        createSolutionStub(
          status = SolutionStatus.InRotation,
          authorId = "aid2"))

      val u = createUserStub()

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
      user.readById(any) returns Some(u)
      user.addEntryToTimeLine(any, any) returns Some(u)
      user.recordBattleParticipation(any, any, any) returns Some(u)

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

      there was one(battle).create(any)
      there was two(solution).updateStatus(any, any, any)
      there was one(user).recordBattleParticipation(mEq("aid1"), any, mEq(List(ss(1)).map(_.id)))
      there was one(user).recordBattleParticipation(mEq("aid2"), any, mEq(List(ss(0)).map(_.id)))
      there were two(user).addEntryToTimeLine(any, any)
      there were two(user).addEntryToTimeLineMulti(any, any)
      result must beAnInstanceOf[OkApiResult[TryCreateBattleRequest]]
    }

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

      result must beAnInstanceOf[OkApiResult[TryCreateBattleResult]]
    }

    "Reward participants" in context {
      val battle = createBattleStub(status = BattleStatus.Resolved)
      val u1 = createUserStub(id = battle.info.battleSides(0).authorId)
      val u2 = createUserStub(id = battle.info.battleSides(1).authorId)

      user.readById(u1.id) returns Some(u1)
      user.readById(u2.id) returns Some(u2)
      user.storeBattleInDailyResult(any, any) returns Some(u1)

      val result = api.rewardBattleParticipants(RewardBattleParticipantsRequest(battle))

      there were two(user).storeBattleInDailyResult(any, any)
      result must beAnInstanceOf[OkApiResult[RewardBattleParticipantsResult]]
    }
  }
}

