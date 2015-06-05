package logic.user

import controllers.domain.app.protocol.ProfileModificationResult._
import logic._
import models.domain.user.Functionality

/**
 * All logic about voting quest solutions is here.
 */
trait VotingSolutions { this: UserLogic =>

  /**
   *
   */
  def canVoteSolution(solutionId: String) = {
    if (!user.profile.rights.unlockedFunctionality.contains(Functionality.VoteSolutions))
      NotEnoughRights
    else if (user.stats.votedSolutions.contains(solutionId))
      InvalidState
    else if (user.stats.createdSolutions.contains(solutionId))
      OutOfContent
    else if (!bioComplete)
      IncompleteBio
    else
      OK
  }
}
