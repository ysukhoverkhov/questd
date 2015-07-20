package logic.user

import java.util.Date

import controllers.domain.app.protocol.ProfileModificationResult
import logic.BaseLogicSpecs
import models.domain.user.battlerequests.{BattleRequestStatus, BattleRequest}
import testhelpers.domainstubs._

class ChallengesSpecs extends BaseLogicSpecs {

  "User Logic for challenges" should {

    "Do not allow auto battles with itself" in {
      applyConfigMock()

      val user = createUserStub()
      val mySolution = createSolutionStub(authorId = user.id)
      val opponentSolution = createSolutionStub(authorId = user.id)

      val rv = user.canAutoCreatedBattle(
        mySolution = mySolution,
        opponentSolution = opponentSolution,
        opponentShouldNotHaveBattles = false,
        checkQuest = true)

      rv must beEqualTo(ProfileModificationResult.OutOfContent)
    }

    "Do not allow auto battles with opponent with battles" in {
      applyConfigMock()

      val me = createUserStub()
      val opponent = createUserStub()
      val mySolution = createSolutionStub(authorId = me.id)
      val opponentSolution = createSolutionStub(authorId = opponent.id, battleIds = List("1"))

      val rv = me.canAutoCreatedBattle(
        mySolution = mySolution,
        opponentSolution = opponentSolution,
        opponentShouldNotHaveBattles = true,
        checkQuest = true)

      rv must beEqualTo(ProfileModificationResult.InvalidState)
    }

    "Do allow auto battles with opponent with battles if allowed" in {
      applyConfigMock()

      val me = createUserStub()
      val opponent = createUserStub()
      val mySolution = createSolutionStub(authorId = me.id)
      val opponentSolution = createSolutionStub(authorId = opponent.id, battleIds = List("1"))

      val rv = me.canAutoCreatedBattle(
        mySolution = mySolution,
        opponentSolution = opponentSolution,
        opponentShouldNotHaveBattles = false,
        checkQuest = true)

      rv must beEqualTo(ProfileModificationResult.OK)
    }

    "Do not allow battles for solutions from different quests" in {
      applyConfigMock()

      val me = createUserStub()
      val opponent = createUserStub()
      val mySolution = createSolutionStub(authorId = me.id, questId = "qid1")
      val opponentSolution = createSolutionStub(authorId = opponent.id, questId = "qid2")

      val rv = me.canAutoCreatedBattle(
        mySolution = mySolution,
        opponentSolution = opponentSolution,
        opponentShouldNotHaveBattles = true,
        checkQuest = true)

      rv must beEqualTo(ProfileModificationResult.InvalidState)
    }

    "Do allow battles for solutions from different quests if allowed" in {
      applyConfigMock()

      val me = createUserStub()
      val opponent = createUserStub()
      val mySolution = createSolutionStub(authorId = me.id, questId = "qid1")
      val opponentSolution = createSolutionStub(authorId = opponent.id, questId = "qid2")

      val rv = me.canAutoCreatedBattle(
        mySolution = mySolution,
        opponentSolution = opponentSolution,
        opponentShouldNotHaveBattles = true,
        checkQuest = false)

      rv must beEqualTo(ProfileModificationResult.OK)
    }

    "Do not allow battles for solutions with active challenge" in {
      applyConfigMock()

      val sol1Id = "s1id"
      val sol2Id = "s2id"

      val opponent = createUserStub()
      val me = createUserStub(battleRequests = List(BattleRequest(opponent.id, sol1Id, sol2Id, BattleRequestStatus.Requests)))
      val mySolution = createSolutionStub(id = sol1Id, authorId = me.id)
      val opponentSolution = createSolutionStub(id = sol2Id, authorId = opponent.id)

      val rv = me.canAutoCreatedBattle(
        mySolution = mySolution,
        opponentSolution = opponentSolution,
        opponentShouldNotHaveBattles = true,
        checkQuest = true)

      rv must beEqualTo(ProfileModificationResult.InvalidState)
    }

    "Do not allow battles for solutions created too early" in {
      applyConfigMock()

      val sol1Id = "s1id"
      val sol2Id = "s2id"

      val opponent = createUserStub()
      val me = createUserStub()
      val mySolution = createSolutionStub(id = sol1Id, authorId = me.id, creationDate = new Date())
      val opponentSolution = createSolutionStub(id = sol2Id, authorId = opponent.id)

      val rv = me.canAutoCreatedBattle(
        mySolution = mySolution,
        opponentSolution = opponentSolution,
        opponentShouldNotHaveBattles = true,
        checkQuest = true)

      rv must beEqualTo(ProfileModificationResult.CoolDown)
    }

  }
}

