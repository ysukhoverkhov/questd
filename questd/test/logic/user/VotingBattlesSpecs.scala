package logic.user

import controllers.domain.app.user.VoteBattleByUserCode
import logic.BaseLogicSpecs
import models.domain.user.profile.Rights
import testhelpers.domainstubs._

class VotingBattlesSpecs extends BaseLogicSpecs {

  "User Logic for battle voting" should {

    "Do not allow voting for battles without rights" in context {
      val user = createUserStub(rights = Rights.none)
      val battle = createBattleStub()

      val rv = user.canVoteBattle(battle.id)

      rv must beEqualTo(VoteBattleByUserCode.NotEnoughRights)
    }

    "Do allow voting for battles not in time line" in context {
      val user = createUserStub()
      val battle = createBattleStub()

      val rv = user.canVoteBattle(battle.id)

      rv must beEqualTo(VoteBattleByUserCode.OK)
    }

    "Do not allow voting for battles in time line but we already voted for" in context {
      val battle = createBattleStub()
      val user = createUserStub(
        votedBattles = Map(battle.id -> ""))

      val rv = user.canVoteBattle(battle.id)

      rv must beEqualTo(VoteBattleByUserCode.BattleAlreadyVoted)
    }

    "Do not allow voting for battle we are participating in" in context {
      val uid = "uid"
      val battle = createBattleStub()
      val user = createUserStub(
        participatedBattles = Map(battle.id -> ""))

      val rv = user.canVoteBattle(battle.id)

      rv must beEqualTo(VoteBattleByUserCode.ParticipantsCantVote)
    }

    "Do not allow voting battles with incomplete bio" in context {
      val battle = createBattleStub()
      val user = createUserStub(cultureId = None)

      val rv = user.canVoteBattle(battle.id)

      rv must beEqualTo(VoteBattleByUserCode.IncompleteBio)
    }
  }
}

