package logic.internal

import logic.constants._
import basefunctions._
import models.domain.Functionality._

object spendcoinsfunctions {

  /**
   * How much player will spend daily.
   */
  def coinToSpentDaily(level: Int): Double = {
    val k = 162.15924
    val d = 4.593018
    val b = -150.641173

    def coinToSpentDailyInt(level: Int, k: Double, d: Double, b: Double) = megaf(level, k, d, b, 0)

    coinToSpentDailyInt(level, k, d, b)
  }

  /**
   * How much player will spend daily.
   */
  def coinToSpentDailyAll(level: Int): Double = {
    coinToSpentDaily(level) * PremiumIncomeMultiplier
  }



  /**
   * How much coins we should spend daily on selecting quests.
   */
  def coinSelectQuest(level: Int): Double = {
    val k = 129.677
    val d = 6.396
    val b = -29.677
    val y = 4.901e-7

    def coinSelectQuestInt(level: Int, k: Double, d: Double, b: Double, y: Double): Double = {
      level match {
        case _ if level < levelFor(SubmitPhotoSolutions) => 0
        case _ if (level < levelFor(SubmitPhotoQuests)) && (level >= levelFor(SubmitPhotoSolutions)) => megaf(level, k, d, b, y)
        case _ => coinSelectQuestInt(levelFor(SubmitPhotoQuests) - 1, k, d, b, y) * 0.63 + megaf(level, k, d, b, y) * 0.37
      }
    }

    coinSelectQuestInt(level, k, d, b, y)
  }

  /**
   * How much we spend on friends daily.
   */
  def coinToSpentDailyFriendsOnly(level: Int): Double = {
    coinToSpentDailyAll(level) - coinToSpentDaily(level)
  }

  /**
   * How much coins we should spend on adding following each day.
   */
  def coinAddShort(level: Int): Double = {
    val k = 29.186304
    val d = 1
    val b = 1
    val y = 1

    def coinAddShortInt(level: Int, k: Double, d: Double, b: Double, y: Double): Double = {
      level match {
        case _ if level < levelFor(AddToFollowing) => 0
        case _ if (level < levelFor(SubmitPhotoSolutions)) && (level >= levelFor(AddToFollowing)) => megaf(level, k, d, b, y)
        case _ => coinAddShortInt(levelFor(SubmitPhotoSolutions) - 1, k, d, b, y)
      }
    }

    coinAddShortInt(level, k, d, b, y)
  }

  /**
   * How much coins we should spend on adding friends each day.
   */
  def coinAddFriend(level: Int): Double = {
    coinToSpentDailyFriendsOnly(level)
  }

}
