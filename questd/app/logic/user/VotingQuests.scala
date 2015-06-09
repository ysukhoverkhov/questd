package logic.user

import logic._
import controllers.domain.app.protocol.ProfileModificationResult._
import models.domain._
import models.domain.user.profile.Functionality

/**
 * All logic about voting quests is here.
 */
trait VotingQuests { this: UserLogic =>

  /**
   * Check is our user can vote for given quest with given vote.
   */
  def canVoteQuest(questId: String) = {
    if (!user.profile.rights.unlockedFunctionality.contains(Functionality.VoteQuests))
      NotEnoughRights
    else if (user.stats.votedQuests.contains(questId))
      InvalidState
    else if (user.stats.createdQuests.contains(questId))
      OutOfContent
    else if (!bioComplete)
      IncompleteBio
    else
      OK
  }
}

