package logic.user

import controllers.domain.app.protocol.ProfileModificationResult._
import logic._
import models.domain.common.ContentVote
import models.domain.user.profile.Functionality

/**
 * All logic about voting quest solutions is here.
 */
trait VotingSolutions { this: UserLogic =>

  /**
   *
   */
  def canVoteSolution(solutionId: String, vote: ContentVote.Value) = {
    if (!user.profile.rights.unlockedFunctionality.contains(Functionality.VoteSolutions))
      NotEnoughRights
    else if (!user.profile.rights.unlockedFunctionality.contains(Functionality.Report) && (vote != ContentVote.Cool))
      NotEnoughRights
    else if (user.stats.votedSolutions.contains(solutionId))
      InvalidState
    else if (user.stats.solvedQuests.values.exists(_ == solutionId))
      OutOfContent
    else if (!bioComplete)
      IncompleteBio
    else
      OK
  }
}
