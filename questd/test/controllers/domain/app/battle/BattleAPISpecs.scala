package controllers.domain.app.battle

import controllers.domain._
import models.domain.{BattleStatus, SolutionStatus}
import org.mockito.Matchers
import testhelpers.domainstubs._

class BattleAPISpecs extends BaseAPISpecs {

  "Battle API" should {

    "updateBattleState updates battle and solutions if battle should be resolved" in context {
      val ss = List(
        createSolutionStub(status = SolutionStatus.Won),
        createSolutionStub(status = SolutionStatus.Won)
      )

      val b = createBattleStub(
        solutionIds = ss.map(_.id),
        status = BattleStatus.Fighting)
      val u = createUserStub()

      solution.readById(ss(0).id) returns Some(ss(0))
      solution.readById(ss(1).id) returns Some(ss(1))

      solution.updateStatus(Matchers.eq(ss(0).id), any, any) returns Some(ss(0).copy(status = SolutionStatus.Won))
      solution.updateStatus(Matchers.eq(ss(1).id), any, any) returns Some(ss(1).copy(status = SolutionStatus.Lost))
      user.storeSolutionInDailyResult(any, any) returns Some(u)

      user.readById(any) returns Some(createUserStub())
      quest.readById(any) returns Some(createQuestStub())

      battle.updateStatus(any, Matchers.eq(BattleStatus.Resolved)) returns Some(b.copy(status = BattleStatus.Resolved))

      val result = api.updateBattleState(UpdateBattleStateRequest(b))

      there were atLeast(4)(solution).readById(any)
      there were two(solution).updateStatus(any, any, any)
      there were two(user).readById(any)
      there was one(battle).updateStatus(any, Matchers.eq(BattleStatus.Resolved))
      there were two(user).storeSolutionInDailyResult(any, any)

      result must beEqualTo(OkApiResult(UpdateBattleStateResult()))
    }

    "Nominate both as winners in case of equal points" in context {
      val ss = List(
        createSolutionStub(status = SolutionStatus.Won),
        createSolutionStub(status = SolutionStatus.Won)
      )

      val b = createBattleStub(
        solutionIds = ss.map(_.id),
        status = BattleStatus.Fighting)
      val u = createUserStub()

      solution.readById(ss(0).id) returns Some(ss(0))
      solution.readById(ss(1).id) returns Some(ss(1))

      solution.updateStatus(Matchers.eq(ss(0).id), any, any) returns Some(ss(0).copy(status = SolutionStatus.Won))
      solution.updateStatus(Matchers.eq(ss(1).id), any, any) returns Some(ss(1).copy(status = SolutionStatus.Lost))
      user.storeSolutionInDailyResult(any, any) returns Some(u)

      user.readById(any) returns Some(createUserStub())
      quest.readById(any) returns Some(createQuestStub())

      battle.updateStatus(any, Matchers.eq(BattleStatus.Resolved)) returns Some(b.copy(status = BattleStatus.Resolved))

      val result = api.updateBattleState(UpdateBattleStateRequest(b))

      there were atLeast(4)(solution).readById(any)
      there were two(solution).updateStatus(any, Matchers.eq(SolutionStatus.Won), any)
      there were two(user).readById(any)
      there was one(battle).updateStatus(any, Matchers.eq(BattleStatus.Resolved))
      there were two(user).storeSolutionInDailyResult(any, any)

      result must beEqualTo(OkApiResult(UpdateBattleStateResult()))
    }

  }
}


