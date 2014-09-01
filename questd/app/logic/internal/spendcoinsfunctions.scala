package logic.internal

import logic.constants._
import basefunctions._
import models.domain.Functionality._

object spendcoinsfunctions {
 
  /**
   * How much player will spend daily.
   */
  def coinToSpentDaily(level: Int): Double = {
    val k = 129.676794
    val d = 6.39558
    val b = -29.676794
    
    def coinToSpentDailyInt(level: Int, k: Double, d: Double, b: Double) = megaf(level, k, d, b, 0)
    
    coinToSpentDailyInt(level, k, d, b)
  }
  
  /**
   * How much coins per day player should spend on shuffling themes for quest proposals.
   */
  def coinsShuffleTheme(level: Int): Double = {
    val k = 27.273588
    val d = 6.395583
    val b = -130.255489 
    val y = -3.699747e-7
    
    if (level < levelFor(SubmitPhotoQuests))
      0
    else
      megaf(level, k, d, b, y)
  }

  /**
   * How much coin per day we should spend on skipping quests.
   */
  def coinShuffleQuest(level: Int): Double = {
    val k = 87.55513
    val d = 6.395567
    val b = 102.373491
    val y = 4.336504e-5
    
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
   * How much we spend on friends daily.
   */
  def coinToSpentDailyFriendsOnly(level: Int): Double = {
    val k = 0.17555787
    coinToSpentDaily(level) * k
  }
  
  /**
   * How much coins we should spend on adding shortlist each day.
   */
  def coinAddShort(level: Int): Double = {
    val k = 29.186304
    val d = 1
    val b = 1
    val y = 1
    
    def coinAddShortInt(level: Int, k: Double, d: Double, b: Double, y: Double): Double = {
      level match {
        case _ if level < levelFor(AddToShortList) => 0
        case _ if (level < levelFor(SubmitPhotoResults)) && (level >= levelFor(AddToShortList)) => megaf(level, k, d, b, y)
        case _ => coinAddShortInt(levelFor(SubmitPhotoResults) - 1, k, d, b, y)
      }
    }
    
    coinAddShortInt(level, k, d, b, y)
  }
  
  /**
   * How much coins we should spend on adding friends each day.
   */
  def coinAddFriend(level: Int): Double = {
    val k = 77.924446
    val d = 6.39558
    val b = -145.644936
    val y = 2.815498e-6
    
    def coinAddFriendInt(level: Int, k: Double, d: Double, b: Double, y: Double): Double = {
      level match {
        case _ if level < levelFor(InviteFriends) => 0
        case _ if (level < levelFor(SubmitPhotoQuests)) && (level >= levelFor(InviteFriends)) => megaf(level, k, d, b, y)
        case _ => coinAddFriendInt(levelFor(SubmitPhotoQuests) - 1, k, d, b, y) * 0.35 + megaf(level, k, d, b, y) * 0.65
      }
    }
    
    coinAddFriendInt(level, k, d, b, y)
  }
  
  
  
}
