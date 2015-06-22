package controllers.domain.app.user

import controllers.domain._
import controllers.domain.app.protocol.ProfileModificationResult
import models.domain.user.battlerequests.{BattleRequestStatus, BattleRequest}
import testhelpers.domainstubs._

//noinspection ZeroIndexToHead
class ChallengesAPISpecs extends BaseAPISpecs {

  "Challenges API" should {

    "challengeBattle stores battle request to both users" in context {
      val mySolutionId = "mysolid"
      val opponentSolutionId = "opsolid"
      val q = createQuestStub()
      val sol1 = createSolutionStub(id = mySolutionId, questId = q.id)
      val sol2 = createSolutionStub(id = opponentSolutionId, questId = q.id)
      val u1 = createUserStub(solvedQuests = Map(q.id -> sol1.id))

      solution.readById(mySolutionId) returns Some(sol1)
      solution.readById(opponentSolutionId) returns Some(sol2)
      user.addBattleRequest(any, any) returns Some(u1)

      val result = api.challengeBattle(ChallengeBattleRequest(
        user = u1,
        mySolutionId = mySolutionId,
        opponentSolutionId = opponentSolutionId))

      result must beEqualTo(OkApiResult(ChallengeBattleResult(ProfileModificationResult.OK, Some(u1.profile))))

      there was one(solution).readById(mySolutionId)
      there was one(solution).readById(opponentSolutionId)
      there were two(user).addBattleRequest(any, any)
    }

    "respondBattleRequest crates battle" in context {
      val mySolutionId = "mysolid"
      val opponentSolutionId = "opsolid"
      val q = createQuestStub()
      val sol1 = createSolutionStub(id = mySolutionId, questId = q.id)
      val sol2 = createSolutionStub(id = opponentSolutionId, questId = q.id)
      val opponent = createUserStub()
      val u1 = createUserStub(solvedQuests = Map(q.id -> sol1.id), battleRequests = List(
        BattleRequest(
          opponentId = opponent.id,
          mySolutionId = sol1.id,
          opponentSolutionId = sol2.id,
          status = BattleRequestStatus.Requests
        )))

      user.updateBattleRequest(any, any, any, any) returns Some(u1)
      solution.readById(sol1.id) returns Some(sol1)
      solution.readById(sol2.id) returns Some(sol2)
      user.readById(any) returns Some(u1)
      user.recordBattleParticipation(any, any, any) returns Some(u1)
      user.addEntryToTimeLine(any, any) returns Some(u1)
      user.addMessage(any, any) returns Some(u1)

      val result = api.respondBattleRequest(RespondBattleRequestRequest(
        user = u1,
        opponentSolutionId = opponentSolutionId,
        accept = true))

      there were two(user).updateBattleRequest(any, any, any, any)
      there was one(battle).create(any)

      result must beEqualTo(OkApiResult(RespondBattleRequestResult(ProfileModificationResult.OK, Some(u1.profile))))
    }
  }
}

