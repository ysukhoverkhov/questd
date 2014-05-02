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
import controllers.domain.admin._
import controllers.domain._

/**
 * All logic about voting quest proposals is here.
 */
trait VotingQuestProposals { this: UserLogic =>

  /**
   *
   */
  def canGetQuestProposalForVote = {
    if (!user.profile.rights.unlockedFunctionality.contains(Functionality.VoteQuestProposals.toString()))
      NotEnoughRights
    else if (user.profile.questProposalVoteContext.reviewingQuest != None)
      InvalidState
    else
      OK
  }

  /**
   *
   */
  def canVoteQuestProposal = {
    if (!user.profile.rights.unlockedFunctionality.contains(Functionality.VoteQuestProposals.toString()))
      NotEnoughRights
    else if (user.profile.questProposalVoteContext.reviewingQuest == None)
      InvalidState
    else
      OK
  }

  /**
   * @return None if no more quests to vote for today.
   */
  def getQuestProposalToVote: Option[Quest] = {
    getRandomQuest(QuestGetReason.ForVoting)
  }

  /**
   * Reward for voting for quest proposal.
   */
  def getQuestProposalVoteReward = {
    val level = user.profile.publicProfile.level
    val count = user.profile.questProposalVoteContext.numberOfReviewedQuests

    if (count < rewardedProposalVotesPerLevel(level))
      (Assets(coins = rewardForVotingProposal(level, count + 1))).clampBot
    else
      Assets()
  }

}