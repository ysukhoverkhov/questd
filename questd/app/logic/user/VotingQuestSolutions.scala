package logic.user

import java.util.Date
import org.joda.time.DateTime
import com.github.nscala_time.time.Imports._
import play.Logger
import logic._
import logic.constants._
import logic.functions._
import controllers.domain.app.protocol.ProfileModificationResult._
import models.domain._
import models.domain.base._
import models.domain.ContentType._
import controllers.domain._
import controllers.domain.app.questsolution._

/**
 * All logic about voting quest solutions is here.
 */
trait VotingQuestSolutions { this: UserLogic =>

  /**
   *
   */
  def canGetQuestSolutionForVote = {
    if (!user.profile.rights.unlockedFunctionality.contains(Functionality.VoteQuestSolutions))
      NotEnoughRights
    else if (user.profile.questSolutionVoteContext.reviewingQuestSolution != None)
      InvalidState
    else
      OK
  }

  /**
   * @return None if no more quests to vote for today.
   */
  def getQuestSolutionToVote: Option[QuestSolution] = {
    getRandomSolution
  }

  /**
   *
   */
  def canVoteQuestSolution = {
    if (!user.profile.rights.unlockedFunctionality.contains(Functionality.VoteQuestSolutions))
      NotEnoughRights
    else if (user.profile.questSolutionVoteContext.reviewingQuestSolution == None)
      InvalidState
    else
      OK
  }

  /**
   * Reward for quest solution.
   */
  def getQuestSolutionVoteReward = {
    val level = user.profile.publicProfile.level
    val count = user.profile.questSolutionVoteContext.numberOfReviewedSolutions

    if (count < rewardedSolutionVotesPerLevel(level))
      Assets(coins = rewardForVotingSolution(level, count + 1)).clampBot
    else
      Assets()
  }
}