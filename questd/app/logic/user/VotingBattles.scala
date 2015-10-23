package logic.user

import controllers.domain.app.user.VoteBattleByUserCode
import logic._
import models.domain.user.profile.Functionality

/**
 * All logic about voting battles.
 */
trait VotingBattles { this: UserLogic =>

  /**
   *
   */
  def canVoteBattle(battleId: String): VoteBattleByUserCode.Value = {
    import VoteBattleByUserCode._

    if (!user.profile.rights.unlockedFunctionality.contains(Functionality.VoteBattles))
      NotEnoughRights
    else if (user.stats.votedBattles.contains(battleId))
      BattleAlreadyVoted
    else if (user.stats.participatedBattles.contains(battleId))
      ParticipantsCantVote
    else if (!bioComplete)
      IncompleteBio
    else
      OK
  }
}
