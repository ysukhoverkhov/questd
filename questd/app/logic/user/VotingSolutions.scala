package logic.user

import logic._
import controllers.domain.app.protocol.ProfileModificationResult._
import models.domain._

/**
 * All logic about voting quest solutions is here.
 */
trait VotingSolutions { this: UserLogic =>

  /**
   *
   */
  def canVoteSolution(solutionId: String) = {
    if (!user.profile.rights.unlockedFunctionality.contains(Functionality.VoteQuestSolutions))
      NotEnoughRights
    else if (user.stats.votedSolutions.contains(solutionId))
      InvalidState
    else if (user.stats.createdSolutions.contains(solutionId))
      OutOfContent
    else if (user.demo.cultureId == None || user.profile.publicProfile.bio.gender == Gender.Unknown)
      IncompleteBio
    else
      OK
  }
}
