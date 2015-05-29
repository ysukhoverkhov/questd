package controllers.domain.app.battle

import java.util.Date

import controllers.domain._
import models.domain.battle.BattleStatus
import models.domain.solution.SolutionStatus
import models.domain.user.dailyresults.BattleResult
import org.mockito.Matchers.{eq => mEq}
import testhelpers.domainstubs._

//noinspection ZeroIndexToHead
class BattleAPISpecs extends BaseAPISpecs {

  "Battle API" should {

    "updateBattleState updates battle and solutions if battle should be resolved" in context {
      val ss = List(
        createSolutionStub(id = "sid1", status = SolutionStatus.InRotation),
        createSolutionStub(id = "sid2", status = SolutionStatus.InRotation)
      )

      val b = createBattleStub(
        solutionIds = ss.map(_.id),
        points = List(20, 1),
        status = BattleStatus.Fighting)
      val u = createUserStub()

      battle.updateStatus(any, any, any) returns Some(b.copy(info = b.info.copy(status = BattleStatus.Resolved)))

      user.readById(any) returns Some(u)
      user.storeBattleInDailyResult(any, any) returns Some(u)

      val result = api.updateBattleState(UpdateBattleStateRequest(b))

      there was one(battle).updateStatus(any, any, any)
      there were two(user).storeBattleInDailyResult(any, any)

      result must beEqualTo(OkApiResult(UpdateBattleStateResult()))
    }

    "Nominate battle side with higher points as winners " in context {
      val ss = List(
        createSolutionStub(id = "sid1", status = SolutionStatus.InRotation),
        createSolutionStub(id = "sid2", status = SolutionStatus.InRotation))
      val uu = List(
        createUserStub(),
        createUserStub())

      val b = createBattleStub(
        solutionIds = ss.map(_.id),
        authorIds = uu.map(_.id),
        winnerIds = List(uu(0).id),
        points = List(20, 1),
        status = BattleStatus.Fighting)

      battle.updateStatus(any, any, any) returns Some(b.copy(info = b.info.copy(status = BattleStatus.Resolved)))

      user.readById(uu(0).id) returns Some(uu(0))
      user.readById(uu(1).id) returns Some(uu(1))
      user.storeBattleInDailyResult(any, any) returns Some(uu(0))

      val result = api.updateBattleState(UpdateBattleStateRequest(b))

      there was one(battle).updateStatus(any, any, any)
      there was one(user).storeBattleInDailyResult(mEq(uu(0).id), mEq(BattleResult(b.id, b.info.victoryReward, isVictory = true)))
      there was one(user).storeBattleInDailyResult(mEq(uu(1).id), mEq(BattleResult(b.id, b.info.defeatReward, isVictory = false)))

      result must beEqualTo(OkApiResult(UpdateBattleStateResult()))
    }

    "Nominate both as winners in case of equal points" in context {
      val ss = List(
        createSolutionStub(id = "sid1", status = SolutionStatus.InRotation),
        createSolutionStub(id = "sid2", status = SolutionStatus.InRotation))
      val uu = List(
        createUserStub(),
        createUserStub())

      val b = createBattleStub(
        solutionIds = ss.map(_.id),
        authorIds = uu.map(_.id),
        winnerIds = uu.map(_.id),
        points = List(20, 1),
        status = BattleStatus.Fighting)

      battle.updateStatus(any, any, any) returns Some(b.copy(info = b.info.copy(status = BattleStatus.Resolved)))

      user.readById(uu(0).id) returns Some(uu(0))
      user.readById(uu(1).id) returns Some(uu(1))
      user.storeBattleInDailyResult(any, any) returns Some(uu(0))

      val result = api.updateBattleState(UpdateBattleStateRequest(b))

      there was one(battle).updateStatus(any, any, any)
      there was one(user).storeBattleInDailyResult(mEq(uu(0).id), mEq(BattleResult(b.id, b.info.victoryReward, isVictory = true)))
      there was one(user).storeBattleInDailyResult(mEq(uu(1).id), mEq(BattleResult(b.id, b.info.defeatReward, isVictory = true)))

      result must beEqualTo(OkApiResult(UpdateBattleStateResult()))
    }

    "voteBattle updates points correctly" in context {
      val ss = List(
        createSolutionStub(id = "sid1", status = SolutionStatus.InRotation),
        createSolutionStub(id = "sid2", status = SolutionStatus.InRotation))
      val uu = List(
        createUserStub(),
        createUserStub())

      val b = createBattleStub(
        solutionIds = ss.map(_.id),
        authorIds = uu.map(_.id),
        winnerIds = uu.map(_.id),
        points = List(20, 1),
        status = BattleStatus.Fighting,
        voteEndDate = new Date(new Date().getTime + 1000000))

      battle.updatePoints(any, any, any, any) returns Some(b)

      val result = api.voteBattle(VoteBattleRequest(b, ss(0).id, isFriend = true))

      there was one(battle).updatePoints(any, mEq(ss(0).id), mEq(0), mEq(1))

      result must beEqualTo(OkApiResult(VoteBattleResult()))
    }
  }
}

