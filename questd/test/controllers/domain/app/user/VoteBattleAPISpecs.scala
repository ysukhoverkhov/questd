package controllers.domain.app.user

import java.util.Date

import controllers.domain.{OkApiResult, BaseAPISpecs}
import models.domain.battle.BattleStatus
import models.domain.solution.SolutionStatus
import testhelpers.domainstubs._

//noinspection ZeroIndexToHead
class VoteBattleAPISpecs extends BaseAPISpecs {

  "Vote Battle API" should {

    "Voting for battle works" in context {

      val uu = List(
        createUserStub(),
        createUserStub())
      val ss = List(
        createSolutionStub(id = "sid1", status = SolutionStatus.InRotation),
        createSolutionStub(id = "sid2", status = SolutionStatus.InRotation))
      val b = createBattleStub(
        solutionIds = ss.map(_.id),
        authorIds = uu.map(_.id),
        winnerIds = uu.map(_.id),
        points = List(20, 1),
        status = BattleStatus.Fighting,
        voteEndDate = new Date(new Date().getTime + 1000000))

      battle.readById(b.id) returns Some(b)
      user.recordBattleVote(uu(0).id, b.id, ss(0).id) returns Some(uu(0))
      battle.updatePoints(any, any, any, any) returns Some(b)

      val result = api.voteBattleByUser(VoteBattleByUserRequest(uu(0), b.id, ss(0).id))

      there was one(battle).readById(b.id)
      there was one(user).recordBattleVote(uu(0).id, b.id, ss(0).id)
      there was one(battle).updatePoints(any, any, any, any)

      result must beAnInstanceOf[OkApiResult[VoteBattleByUserResult]]
    }
  }
}
