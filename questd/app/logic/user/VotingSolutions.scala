package logic.user

import controllers.domain.app.user.VoteSolutionByUserCode
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
  def canVoteSolution(solutionId: String, vote: ContentVote.Value): VoteSolutionByUserCode.Value = {
    import VoteSolutionByUserCode._

    if (!user.profile.rights.unlockedFunctionality.contains(Functionality.VoteSolutions))
      NotEnoughRights
    else if (!user.profile.rights.unlockedFunctionality.contains(Functionality.Report) && (vote != ContentVote.Cool))
      NotEnoughRights
    else if (user.stats.votedSolutions.contains(solutionId))
      SolutionAlreadyVoted
    else if (user.stats.solvedQuests.values.exists(_ == solutionId))
      CantVoteOwnSolution
    else if (!bioComplete)
      IncompleteBio
    else
      OK
  }
}
