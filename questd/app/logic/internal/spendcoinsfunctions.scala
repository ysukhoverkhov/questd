package logic.internal

import logic.constants._
import basefunctions._
import models.domain.Functionality._

object spendcoinsfunctions {
 
  /**
   * How much player will spend daily.
   */
  def coinToSpentDaily(level: Int): Double = {
    // TODO more digits after points here.
    val k = 129.677
    val d = 6.396
    val b = -29.677
    
    def coinToSpentDailyInt(level: Int, k: Double, d: Double, b: Double) = megaf(level, k, d, b, 0)
    
    coinToSpentDailyInt(level, k, d, b)
  }
  
  /**
   * How much coins per day player should spend on shuffling themes for quest proposals.
   */
  def coinsShuffleTheme(level: Int): Double = {
    val k = 157.364
    val d = 11.542
    val b = -228.653 
    val y = -13.223
    
    if (level < levelFor(SubmitPhotoQuests))
      0
    else
      megaf(level, k, d, b, y)
  }

  /**
   * How much coin per day we should spend on skipping quests.
   */
  def coinShuffleQuest(level: Int): Double = {
    val k = 87.556
    val d = 6.396
    val b = -20.037
    val y = 1.387e-6
    
    def coinShuffleQuestInt(level: Int, k: Double, d: Double, b: Double, y: Double): Double = {
      level match {
        case _ if level < levelFor(SubmitPhotoResults) => 0
        case _ if (level < levelFor(InviteFriends)) && (level >= levelFor(SubmitPhotoResults)) => megaf(level, k, d, b, y)
        case _ => coinShuffleQuestInt(levelFor(InviteFriends) - 1, k, d, b, y) * 0.89 + megaf(level, k, d, b, y) * 0.11
      }
    }
    
    coinShuffleQuestInt(level, k, d, b, y)
  }

  /**
   * How much coins per day we should spend on proposing quests per day at level.
   */
  def coinProposeQuest(level: Int): Double = {
    val k = 81.696
    val d = 6.396
    val b = -390.173 
    val y = -6.644e-6
    
    if (level < levelFor(SubmitPhotoQuests))
      0
    else
      megaf(level, k, d, b, y)
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
        case _ if level < levelFor(SubmitPhotoResults) => 0
        case _ if (level < levelFor(SubmitPhotoQuests)) && (level >= levelFor(SubmitPhotoResults)) => megaf(level, k, d, b, y)
        case _ => coinSelectQuestInt(levelFor(SubmitPhotoQuests) - 1, k, d, b, y) * 0.63 + megaf(level, k, d, b, y) * 0.37
      }
    }
    
    coinSelectQuestInt(level, k, d, b, y)
  }
  
  /**
   * How much we spend on firends daily.
   */
  def coinToSpentDailyFriendsOnly(level: Int): Double = {
    val k = 0.176
    coinToSpentDaily(level) * k
  }
  
  /**
   * How much coins we should spend on adding shortlist each day.
   */
  def coinAddShort(level: Int): Double = {
    val k = 65.766
    val d = 8.788
    val b = -102.658
    val y = -3.933
    
    def coinAddShortInt(level: Int, k: Double, d: Double, b: Double, y: Double): Double = {
      level match {
        case _ if level < levelFor(AddToShortList) => 0
        case _ if (level < levelFor(SubmitPhotoQuests)) && (level >= levelFor(AddToShortList)) => megaf(level, k, d, b, y)
        case _ => coinAddShortInt(levelFor(SubmitPhotoQuests) - 1, k, d, b, y)/* * 1.00 + megaf(level, k, d, b, y) * 0.0*/
      }
    }
    
    coinAddShortInt(level, k, d, b, y)
  }
  
  /**
   * How much coins we should spend on adding friends each day.
   */
  def coinAddFriend(level: Int): Double = {
    val k = 55.587
    val d = 5.695
    val b = -128.005
    val y = 3.154
    
    def coinAddFriendInt(level: Int, k: Double, d: Double, b: Double, y: Double): Double = {
      level match {
        case _ if level < levelFor(InviteFriends) => 0
        case _ if (level < levelFor(AddToShortList)) && (level >= levelFor(InviteFriends)) => megaf(level, k, d, b, y)
        case _ => coinAddFriendInt(levelFor(AddToShortList) - 1, k, d, b, y) * 0.35 + megaf(level, k, d, b, y) * 0.65
      }
    }
    
    coinAddFriendInt(level, k, d, b, y)
  }
  
  
  
}
