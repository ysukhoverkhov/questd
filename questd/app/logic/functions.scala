package logic

import internal.gainratingfunctions._
import internal.gaincoinsfunctions._
import internal.spendcoinsfunctions._
import internal.spendratingfunctions._
import logic.internal.basefunctions._
import models.domain.Functionality._
import constants._

object functions {

  /**
   * ************************
   * Proposing quests.
   * ************************
   */

  /**
   * Period in days to give players a task to make quest.
   */
  def questProposalPeriod(level: Int): Int = 7

  /**
   * Takes proposal period into account.
   * @return Cost for inviting one friend to help with proposal.
   */
  def coinsToInviteFriendForVoteQuestProposal(level: Int): Int = {
    // THis will be redone anyways.
    4
  }

  /**
   * *********************
   * Purchasing of quests.
   * *********************
   */

  /**
   * How much coins does it takes to take quest for solving.
   */
  def coinSelectQuest(level: Int): Int = {

    def coinSelectQuestInt(level: Int, k: Double, d: Double, b: Double) = megaf(level, k, d, b, 0)

    val k = 162.15924
    val d = 4.593018
    val b = -150.641173

    math.round(coinSelectQuestInt(level, k, d, b)).toInt
  }

  /**
   * @return Cost for inviting one friend to help with solution.
   */
  def coinsToInviteFriendForVoteQuestSolution(level: Int): Int = {
    // THis will be redone anyways.
    4
  }

  /**
   * @param level level of the quest.
   * How much rating we will receive for losing quest.
   */
  def ratingToLoseQuest(level: Int): Int = {
    math.round(ratingForSubmitResult(level) * QuestLosingMultiplier).toInt
  }

  /**
   * @param level level of the quest.
   * How much rating we will receive for winning quest.
   */
  def ratingToWinQuest(level: Int): Int = {
    ratingToLoseQuest(level) * QuestVictoryMultiplier
  }

  /**
   * ***********************
   * Voting quest proposals.
   * ***********************
   */

  /**
   * Number of rewarded proposal votes per level.
   */
  def rewardedProposalVotesPerLevel(level: Int): Int = {
    math.floor(4 * math.pow(level + 1 - levelFor(VoteQuests), 0.39)).toInt
  }

  /**
   * How much rating we will receive for voting quest proposal.
   */
  def rewardForVotingProposal(level: Int, voteNumber: Int): Int = {

    def kf(level: Int): Double = coinForVoteProposal(level) / (1 to rewardedProposalVotesPerLevel(level)).map(x => rewardForVotingProposalInt(level, x, 1)).sum

    def rewardForVotingProposalInt(level: Int, voteNumber: Int, k: Double) = {
      k * rewardFunction(voteNumber.toDouble / (rewardedProposalVotesPerLevel(level) + 1))
    }

    math.round(rewardForVotingProposalInt(level, voteNumber, kf(level)).toFloat)
  }

  /**
   * ***********************
   * Voting quest solutions.
   * ***********************
   */

  /**
   * Number of rewarded solution votes per level.
   */
  def rewardedSolutionVotesPerLevel(level: Int): Int = {
    math.floor(10 * math.pow(level + 1 - levelFor(VoteQuestSolutions), 0.3)).toInt
  }

  /**
   * How much rating we will receive for voting quest solution.
   */
  def rewardForVotingSolution(level: Int, voteNumber: Int): Int = {

    def kf(level: Int): Double = coinForVoteResult(level) / (1 to rewardedSolutionVotesPerLevel(level)).map(x => rewardForVotingSolutionInt(level, x, 1)).sum

    def rewardForVotingSolutionInt(level: Int, voteNumber: Int, k: Double) = {
      k * rewardFunction(voteNumber.toDouble / (rewardedSolutionVotesPerLevel(level) + 1))
    }

    math.max(1, math.round(rewardForVotingSolutionInt(level, voteNumber, kf(level)).toFloat))
  }

  /**
   * *************************
   * Daily results
   * *************************
   */

  def dailyRatingDecrease(level: Int): Int = {
    math.round(ratDecrease(level)).toInt
  }

  def dailyCoinsSalary(level: Int): Int = {
    math.round(coinForVoteResult(level)).toInt
  }

  /**
   * ********************
   * Leveling up
   * ********************
   */

  def ratToGainLevel(level: Int): Int = {

    def ratToGainLevelInt(level: Int, k: Double, d: Double, b: Double) = megaf(level, k, d, b, 0)

    val k = 307.3998426
    val d = 2.5694326
    val b = -207.3998426

    if (level <= 1) 0 else math.round(ratToGainLevelInt(level, k, d, b)).toInt
  }

  /**
   * **********************
   * Friends
   * **********************
   */

  def maxNumberOfFriendsOnLevel(level: Int): Int = {
    math.round((NumberOfFreindsOnLastLevel / coinToSpentDailyFriendsOnly(MaxLevel)) * coinToSpentDailyFriendsOnly(level)).toInt
  }

  def costToInviteFriend(level: Int, levelDifference: Int): Int = {

    def costToInviteFriendCoef(levelDif: Int) = {
      math.pow(0.78475, levelDif)
    }

    math.round(costToInviteFriendCoef(levelDifference) * coinAddFriend(level)).toInt
  }

  /**
   * ********************
   * Following
   * ********************
   */
  def costToFollowPerson(level: Int): Int = {
    0
  }

}

