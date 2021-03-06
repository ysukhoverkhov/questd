package logic.user

import controllers.domain.app.protocol.ProfileModificationResult
import logic.BaseLogicSpecs
import models.domain.user.profile.Rights
import testhelpers.domainstubs._

class VotingBattlesSpecs extends BaseLogicSpecs {

  "User Logic for battle voting" should {

    "Do not allow voting for battles without rights" in {
      applyConfigMock()

      val user = createUserStub(rights = Rights.none)
      val battle = createBattleStub()

      val rv = user.canVoteBattle(battle.id)

      rv must beEqualTo(ProfileModificationResult.NotEnoughRights)
    }

    "Do allow voting for battles not in time line" in {
      applyConfigMock()

      val user = createUserStub()
      val battle = createBattleStub()

      val rv = user.canVoteBattle(battle.id)

      rv must beEqualTo(ProfileModificationResult.OK)
    }

    "Do not allow voting for battles in time line but we already voted for" in {
      applyConfigMock()

      val battle = createBattleStub()
      val user = createUserStub(
        votedBattles = Map(battle.id -> ""))

      val rv = user.canVoteBattle(battle.id)

      rv must beEqualTo(ProfileModificationResult.InvalidState)
    }

    "Do not allow voting for battle we are participating in" in {
      applyConfigMock()

      val uid = "uid"
      val battle = createBattleStub()
      val user = createUserStub(
        participatedBattles = Map(battle.id -> ""))

      val rv = user.canVoteBattle(battle.id)

      rv must beEqualTo(ProfileModificationResult.InvalidState)
    }

    "Do not allow voting battles with incomplete bio" in {
      applyConfigMock()

      val battle = createBattleStub()
      val user = createUserStub(cultureId = None)

      val rv = user.canVoteBattle(battle.id)

      rv must beEqualTo(ProfileModificationResult.IncompleteBio)
    }
  }
}

