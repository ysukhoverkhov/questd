package logic.user

import logic._
import logic.functions._
import controllers.domain.app.protocol.ProfileModificationResult._
import models.domain._

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
    else if (user.demo.cultureId == None || user.profile.publicProfile.bio.gender == Gender.Unknown)
      IncompleteBio
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
