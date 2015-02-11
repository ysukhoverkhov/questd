package logic.user

import logic._
import controllers.domain.app.protocol.ProfileModificationResult._
import models.domain._

/**
 * All logic about voting quests is here.
 */
trait VotingQuests { this: UserLogic =>

  /**
   * Check is our user can vote for given quest with given vote.
   */
  def canVoteQuest(questId: String) = {
    val questFromTimeLine = user.timeLine.find { te =>
      (te.objectId == questId) && (te.actorId != user.id)
    }

    if (!user.profile.rights.unlockedFunctionality.contains(Functionality.VoteQuests))
      NotEnoughRights
    else if (questFromTimeLine == None)
      OutOfContent
    else if (questFromTimeLine.get.ourVote != None)
      InvalidState
    else if (user.demo.cultureId == None || user.profile.publicProfile.bio.gender == Gender.Unknown)
      IncompleteBio
    else
      OK
  }
}

