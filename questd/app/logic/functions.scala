package logic

import internal.gainratingfunctions._
import internal.gaincoinsfunctions._
import internal.spendcoinsfunctions._
import logic.internal.basefunctions._
import constants._

object functions {

  /**
   * ************************
   * Creating quests.
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
   * Solving quests.
   * *********************
   */

  /**
   * How much coins does it takes to solve quest.
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
   * *************************
   * Quests income
   * *************************
   */
  def dailyQuestPassiveIncome: Int = {
    50
  }


  /**
   * *************************
   * Daily results
   * *************************
   */

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
    math.floor(((NumberOfFriendsOnLastLevel - 1) / coinToSpentDailyFriendsOnly(MaxLevel)) * coinToSpentDailyFriendsOnly(level) + 1).toInt
  }

  /**
   * Calculates cost to invite a friend to become our friend.
   * @param level level of a potential friend.
   * @return cost in coins.
   */
  def costToInviteFriend(level: Int): Int = {
    math.round(coinAddFriend(level)).toInt
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

