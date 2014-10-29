package logic.user

import logic._
import controllers.domain.app.protocol.ProfileModificationResult._
import models.domain._

/**
 * All logic about voting quest solutions is here.
 */
trait VotingSolutions { this: UserLogic =>

  /**
   * @return None if no more quests to vote for today.
   */
  def getQuestSolutionForTimeLine: Option[QuestSolution] = {
    getRandomSolution
  }

  /**
   *
   */
  def canVoteSolution(solutionId: String) = {
    val solutionFromTimeLine = user.timeLine.find { te =>
      (te.objectId == solutionId) && (te.objectAuthorId != user.id)
    }

    if (!user.profile.rights.unlockedFunctionality.contains(Functionality.VoteQuestSolutions))
      NotEnoughRights
    else if (solutionFromTimeLine == None)
      OutOfContent
    else if (solutionFromTimeLine.get.ourVote != None)
      InvalidState
    else if (user.demo.cultureId == None || user.profile.publicProfile.bio.gender == Gender.Unknown)
      IncompleteBio
    else
      OK
  }
}
