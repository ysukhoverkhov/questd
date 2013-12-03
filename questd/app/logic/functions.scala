package logic

import internal.gainratingfunctions._
import internal.gaincoinsfunctions._
import internal.spendcoinsfunctions._

import logic.internal.basefunctions._

object functions {

  import constants._

  /**
   * ************************
   * Proposing quests.
   * ************************
   */

  /**
   * Rating to give user for successful (approved) proposal at a level.
   */
  def ratingForProposalAtLevel(level: Int): Int = {
    val proposalPeriodAtMaxLevel = 2

    (proposalPeriodAtMaxLevel * ratingForSubmitProposal(level) * math.pow(maxLevel.toDouble / level, 3)).toInt
  }

  /**
   * Period in days to give players a task to make quest.
   */
  def questProposalPeriod(level: Int): Int = math.round(ratingForProposalAtLevel(level).toFloat / ratingForSubmitProposal(level).toFloat)

  /**
   * Cost to skip a single proposal
   */
  def costToSkipProposal(level: Int, skipNumber: Int): Int = {

    def costToSkipProposalInt(level: Int, skipNumber: Int, k: Double) = {
      k * math.pow(5.0 / 3.0, skipNumber)
    }

    def kf(level: Int) = {
      coinsShuffleTheme(level) / (1 to numberOfThemesSkipsForCoins).map(x => costToSkipProposalInt(level, x, 1)).sum
    }

    math.round(costToSkipProposalInt(level, skipNumber, kf(level)) * questProposalPeriod(level)).toInt
  }

  /**
   * Cost to propose a single quest.
   */
  def costToTakeQuestTheme(level: Int): Int = {
    math.round(coinProposeQuest(level) * questProposalPeriod(level)).toInt
  }

  /**
   * Cost to give up quest proposal.
   */
  def ratingToGiveUpQuestProposal(level: Int): Int = {
    math.round(ratingForSubmitProposal(level) * questProposalPeriod(level) * questProposalGiveUpPenalty).toInt
  }

  /**
   * *********************
   * Purchasing of quests.
   * *********************
   */

  /**
   * Cost to skip a single proposal
   */
  def costToSkipQuest(level: Int, skipNumber: Int, currentQuestDuration: Int): Int = {

    def costToSkipQuestInt(level: Int, skipNumber: Int, k: Double) = {
      k * math.pow(4.0 / 3.0, skipNumber)
    }

    def kf(level: Int) = {
      coinShuffleQuest(level) / (1 to numberOfQuestsSkipsForCoins).map(x => costToSkipQuestInt(level, x, 1)).sum
    }

    math.round(costToSkipQuestInt(level, skipNumber, kf(level)) * currentQuestDuration).toInt
  }

  /**
   * How much coins does it takes to take quest for solving.
   */
  def costToTakeQuestToSolve(level: Int, questDuration: Int): Int = {
    math.round(coinSelectQuest(level) * questDuration).toInt
  }

  /**
   * How much in rating we will lose in case of giving quest up.
   */
  def ratingToGiveUpQuest(level: Int, questDuration: Int): Int = {
    math.round(ratingForSubmitResult(level) * questDuration).toInt
  }

  /**
   * ***********************
   * Voting quest proposals.
   * ***********************
   */
  /**
   * How much rating we will receive for voting quest proposal.
   */
  def rewardForVotingProposal(level: Int, voteNumber: Int): Int = {

    def rewardedProposalVotesPerLevel(level: Int): Int = {
      math.floor(4 * math.pow((level + 1 - constants.voteQuestProposals), 0.39)).toInt
    }

    def kf(level: Int): Double = coinForVoteProposal(level) / (1 to rewardedProposalVotesPerLevel(level)).map(x => rewardForVotingProposalInt(level, x, 1)).sum

    def rewardForVotingProposalInt(level: Int, voteNumber: Int, k: Double) = {
      k * rewardFunction(voteNumber.toDouble / (rewardedProposalVotesPerLevel(level) + 1))
    }

    math.round(rewardForVotingProposalInt(level, voteNumber, kf(level)).toFloat)
  }
}
