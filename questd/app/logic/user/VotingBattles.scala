package logic.user

import controllers.domain.app.protocol.ProfileModificationResult._
import logic._
import models.domain.user.profile.Functionality

/**
 * All logic about voting battles.
 */
trait VotingBattles { this: UserLogic =>

  /**
   *
   */
  def canVoteBattle(battleId: String) = {
    if (!user.profile.rights.unlockedFunctionality.contains(Functionality.VoteBattles))
      NotEnoughRights
    else if (user.stats.votedBattles.contains(battleId))
      InvalidState
    else if (user.stats.participatedBattles.contains(battleId))
      InvalidState
    else if (!bioComplete)
      IncompleteBio
    else
      OK
  }
}
