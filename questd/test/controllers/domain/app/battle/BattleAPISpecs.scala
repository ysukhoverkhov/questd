package controllers.domain.app.battle

import controllers.domain._
import org.mockito.Matchers.{eq => mEq}

//noinspection ZeroIndexToHead
class BattleAPISpecs extends BaseAPISpecs {

  "Battle API" should {

    // TODO: uncomment me.
//    "updateBattleState updates battle and solutions if battle should be resolved" in context {
//      val ss = List(
//        createSolutionStub(id = "sid1", status = SolutionStatus.Won, points = 20),
//        createSolutionStub(id = "sid2", status = SolutionStatus.Lost, points = 1)
//      )
//
//      val b = createBattleStub(
//        solutionIds = ss.map(_.id),
//        status = BattleStatus.Fighting)
//      val u = createUserStub()
//
//      solution.readById(ss(0).id) returns Some(ss(0))
//      solution.readById(ss(1).id) returns Some(ss(1))
//
//      solution.updateStatus(mEq(ss(0).id), any, any) returns Some(ss(0).copy(status = SolutionStatus.Won))
//      solution.updateStatus(mEq(ss(1).id), any, any) returns Some(ss(1).copy(status = SolutionStatus.Lost))
//      user.storeSolutionInDailyResult(any, any) returns Some(u)
//
//      user.readById(any) returns Some(createUserStub())
//      quest.readById(any) returns Some(createQuestStub())
//
//      battle.updateStatus(any, mEq(BattleStatus.Resolved), any) returns Some(b.copy(info = b.info.copy(status = BattleStatus.Resolved)))
//
//      val result = api.updateBattleState(UpdateBattleStateRequest(b))
//
//      there were atLeast(4)(solution).readById(any)
//      there were two(solution).updateStatus(any, any, any)
//      there were two(user).readById(any)
//      there was one(battle).updateStatus(any, mEq(BattleStatus.Resolved), mEq(List(ss(0).info.authorId)))
//      there were two(user).storeSolutionInDailyResult(any, any)
//
//      result must beEqualTo(OkApiResult(UpdateBattleStateResult()))
//    }
//
//    "Nominate solution with higher points as winners " in context {
//      val ss = List(
//        createSolutionStub(id = "sid1", status = SolutionStatus.Won, points = 20),
//        createSolutionStub(id = "sid2", status = SolutionStatus.Lost, points = 1)
//      )
//
//      val b = createBattleStub(
//        solutionIds = ss.map(_.id),
//        status = BattleStatus.Fighting)
//      val u = createUserStub()
//
//      solution.readById(ss(0).id) returns Some(ss(0))
//      solution.readById(ss(1).id) returns Some(ss(1))
//
//      solution.updateStatus(mEq(ss(0).id), mEq(SolutionStatus.Won), any) returns Some(ss(0).copy(status = SolutionStatus.Won))
//      solution.updateStatus(mEq(ss(1).id), mEq(SolutionStatus.Lost), any) returns Some(ss(1).copy(status = SolutionStatus.Lost))
//      user.storeSolutionInDailyResult(any, any) returns Some(u)
//
//      user.readById(any) returns Some(createUserStub())
//      quest.readById(any) returns Some(createQuestStub())
//
//      battle.updateStatus(any, mEq(BattleStatus.Resolved), any) returns Some(b.copy(info = b.info.copy(status = BattleStatus.Resolved)))
//
//      val result = api.updateBattleState(UpdateBattleStateRequest(b))
//
//      there were atLeast(4)(solution).readById(any)
//      there were one(solution).updateStatus(mEq(ss(0).id), mEq(SolutionStatus.Won), any)
//      there were one(solution).updateStatus(mEq(ss(1).id), mEq(SolutionStatus.Lost), any)
//      there were two(user).readById(any)
//      there was one(battle).updateStatus(any, mEq(BattleStatus.Resolved), mEq(List(ss(0).info.authorId)))
//      there was one(user).storeSolutionInDailyResult(any, mEq(SolutionResult(
//        solutionId = ss(0).id,
//        battleId = Some(b.id),
//        reward = Some(Assets(0,0,0)),
//        penalty = None,
//        status = SolutionStatus.Won
//      )))
//      there was one(user).storeSolutionInDailyResult(any, mEq(SolutionResult(
//        solutionId = ss(1).id,
//        battleId = Some(b.id),
//        reward = Some(Assets(0,0,0)),
//        penalty = None,
//        status = SolutionStatus.Lost
//      )))
//
//      result must beEqualTo(OkApiResult(UpdateBattleStateResult()))
//    }
//
//    "Nominate both as winners in case of equal points" in context {
//      val ss = List(
//        createSolutionStub(id = "sid1", status = SolutionStatus.Won),
//        createSolutionStub(id = "sid2", status = SolutionStatus.Won)
//      )
//
//      val b = createBattleStub(
//        solutionIds = ss.map(_.id),
//        status = BattleStatus.Fighting)
//      val u = createUserStub()
//
//      solution.readById(ss(0).id) returns Some(ss(0))
//      solution.readById(ss(1).id) returns Some(ss(1))
//
//      solution.updateStatus(mEq(ss(0).id), any, any) returns Some(ss(0).copy(status = SolutionStatus.Won))
//      solution.updateStatus(mEq(ss(1).id), any, any) returns Some(ss(1).copy(status = SolutionStatus.Won))
//      user.storeSolutionInDailyResult(any, any) returns Some(u)
//
//      user.readById(any) returns Some(createUserStub())
//      quest.readById(any) returns Some(createQuestStub())
//
//      battle.updateStatus(any, mEq(BattleStatus.Resolved), mEq(List(ss(0).info.authorId, ss(1).info.authorId))) returns Some(b.copy(info = b.info.copy(status = BattleStatus.Resolved)))
//
//      val result = api.updateBattleState(UpdateBattleStateRequest(b))
//
//      there were atLeast(4)(solution).readById(any)
//      there were two(solution).updateStatus(any, mEq(SolutionStatus.Won), any)
//      there were two(user).readById(any)
//      there was one(battle).updateStatus(any, mEq(BattleStatus.Resolved), mEq(List(ss(0).info.authorId, ss(1).info.authorId)))
//      there were two(user).storeSolutionInDailyResult(any, any)
//
//      result must beEqualTo(OkApiResult(UpdateBattleStateResult()))
//    }
  }
}

