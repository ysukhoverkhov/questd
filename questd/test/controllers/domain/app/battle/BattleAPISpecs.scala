package controllers.domain.app.battle

import java.util.Date

import controllers.domain._
import models.domain.battle.BattleStatus
import models.domain.solution.SolutionStatus
import models.domain.user.dailyresults.BattleResult
import org.mockito.Matchers.{eq => mEq}
import org.mockito.Mockito._
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
      val q = createQuestStub()

      battle.updateStatus(any, any, any) returns Some(b.copy(info = b.info.copy(status = BattleStatus.Resolved)))

      user.readById(any) returns Some(u)
      quest.readById(any) returns Some(q)
      user.storeBattleInDailyResult(any, any) returns Some(u)
      doReturn(OkApiResult(TuneBattlePointsBeforeResolveResult(b))).when(api).tuneBattlePointsBeforeResolve(any)

      val result = api.updateBattleState(UpdateBattleStateRequest(b))

      result must beAnInstanceOf[OkApiResult[UpdateBattleStateResult]]
      there was one(battle).updateStatus(any, any, any)
      there were two(user).storeBattleInDailyResult(any, any)
      there was one(api).tuneBattlePointsBeforeResolve(any)
    }

    "Nominate battle side with higher points as winners " in context {
      val q = createQuestStub()
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
        status = BattleStatus.Fighting,
        questId = q.id)

      battle.updateStatus(any, any, any) returns Some(b.copy(info = b.info.copy(status = BattleStatus.Resolved)))
      doReturn(OkApiResult(TuneBattlePointsBeforeResolveResult(b))).when(api).tuneBattlePointsBeforeResolve(any)

      quest.readById(any) returns Some(q)
      user.readById(uu(0).id) returns Some(uu(0))
      user.readById(uu(1).id) returns Some(uu(1))
      user.storeBattleInDailyResult(any, any) returns Some(uu(0))

      val result = api.updateBattleState(UpdateBattleStateRequest(b))

      result must beAnInstanceOf[OkApiResult[UpdateBattleStateResult]]
      there was one(battle).updateStatus(any, any, any)
      there was one(user).storeBattleInDailyResult(mEq(uu(0).id), mEq(BattleResult(b.id, q.info.victoryReward, isVictory = true)))
      there was one(user).storeBattleInDailyResult(mEq(uu(1).id), mEq(BattleResult(b.id, q.info.defeatReward, isVictory = false)))
      there was one(api).tuneBattlePointsBeforeResolve(any)
    }

    "Nominate both as winners in case of equal points" in context {
      val q = createQuestStub()
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
        questId = q.id)

      battle.updateStatus(any, any, any) returns Some(b.copy(info = b.info.copy(status = BattleStatus.Resolved)))
      doReturn(OkApiResult(TuneBattlePointsBeforeResolveResult(b))).when(api).tuneBattlePointsBeforeResolve(any)

      quest.readById(any) returns Some(q)
      user.readById(uu(0).id) returns Some(uu(0))
      user.readById(uu(1).id) returns Some(uu(1))
      user.storeBattleInDailyResult(any, any) returns Some(uu(0))

      val result = api.updateBattleState(UpdateBattleStateRequest(b))

      result must beAnInstanceOf[OkApiResult[UpdateBattleStateResult]]
      there was one(battle).updateStatus(any, any, any)
      there was one(user).storeBattleInDailyResult(mEq(uu(0).id), mEq(BattleResult(b.id, q.info.victoryReward, isVictory = true)))
      there was one(user).storeBattleInDailyResult(mEq(uu(1).id), mEq(BattleResult(b.id, q.info.victoryReward, isVictory = true)))
      there was one(api).tuneBattlePointsBeforeResolve(any)
    }

    "tuneBattlePointsBeforeResolve adds points to Battles With small amount of votes" in context {
      val b = createBattleStub()

      battle.updatePoints(
        any,
        any,
        any,
        any) returns Some(b)

      val result = api.tuneBattlePointsBeforeResolve(TuneBattlePointsBeforeResolveRequest(b))

      result must beAnInstanceOf[OkApiResult[TuneBattlePointsBeforeResolveResult]]

      there were two(battle).updatePoints(any, any, any, any)
    }

    "tuneBattlePointsBeforeResolve does not add points if there were at least one vote" in context {
      val b = createBattleStub(points = List(0, 1))

      battle.updatePoints(
        any,
        any,
        any,
        any) returns Some(b)

      val result = api.tuneBattlePointsBeforeResolve(TuneBattlePointsBeforeResolveRequest(b))

      result must beAnInstanceOf[OkApiResult[TuneBattlePointsBeforeResolveResult]]

      there were no(battle).updatePoints(any, any, any, any)
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

