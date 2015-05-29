package logic

import logic.constants._
import logic.internal.basefunctions._
import logic.internal.gainratingfunctions._
import logic.internal.gaincoinsfunctions._
import logic.internal.spendcoinsfunctions._

object functions {

  /**
   * ************************
   * Creating quests.
   * ************************
   */

  /**
   * Period in days to give players a task to make quest.
   */
  def questCreationPeriod(level: Int): Int = 7

  /**
   * Takes proposal period into account.
   * @return Cost for inviting one friend to help with quest.
   */
  def coinsToInviteFriendForVoteQuest(level: Int): Int = {
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
  def ratingToSolveQuest(level: Int): Int = {
    math.round(ratingForSubmitResult(level) * QuestLosingMultiplier).toInt
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

  /**
   * Passive income for each of our quests.
   * @return the income.
   */
  def dailyQuestPassiveIncome: Int = 50

  /**
   * Return income for likes of quests.
   * @param likesCount Number of likes quest received so far.
   * @return Coins for likes.
   */
  def dailyQuestIncomeForLikes(likesCount: Int): Int = {
    import Math._
    min(100, ceil(likesCount * 0.5)).toInt
  }

  /**
   * Coins income for quest solving.
   * @return Number of coins as a reward
   */
  def questIncomeForSolving: Int = 25

  /**
   * *************************
   * Tasks income
   * *************************
   */

  def dailyTasksCoinsSalary(level: Int): Int = math.round(coinForTasks(level)).toInt


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

