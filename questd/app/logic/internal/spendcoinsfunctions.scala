package logic.internal

import logic.constants._
import basefunctions._

object spendcoinsfunctions {
 
  /**
   * How much coins per day player should spend on shuffling themes for quest proposals.
   * TODO test me.
   */
  def coinsShuffleTheme(level: Int): Double = {
    val k = 157.364
    val d = 11.542
    val b = -228.653 
    val y = -13.223
    
    if (level < submitPhotoQuests)
      0
    else
      megaf(level, k, d, b, y)
  }

  /**
   * How much coin per day we should spend on skipping quests.
   * TODO test me.
   */
  def coinShuffleQuest(level: Int): Double = {
    val k = 87.556
    val d = 6.396
    val b = -20.037
    val y = 1.387e-6
    
    def coinShuffleQuestInt(level: Int, k: Double, d: Double, b: Double, y: Double): Double = {
      level match {
        case _ if level < submitPhotoResults => 0
        case _ if (level < inviteFriends) && (level >= submitPhotoResults) => megaf(level, k, d, b, y)
        case _ => coinShuffleQuestInt(inviteFriends - 1, k, d, b, y) * 0.89 + megaf(level, k, d, b, y) * 0.11
      }
    }
    
    coinShuffleQuestInt(level, k, d, b, y)
  }

  /**
   * How much coins per day we should spend on proposing quests per day at level.
   * TODO test me.
   */
  def coinProposeQuest(level: Int): Double = {
    val k = 81.696
    val d = 6.396
    val b = -390.173 
    val y = -6.644e-6
    
    if (level < submitPhotoQuests)
      0
    else
      megaf(level, k, d, b, y)
  }
  
  /**
   * How much coins we should spend daily on selecting quests.
   * TODO test me.
   */
  def coinSelectQuest(level: Int): Double = {
    val k = 129.677
    val d = 6.396
    val b = -29.677
    val y = 4.901e-7
    
    def coinSelectQuestInt(level: Int, k: Double, d: Double, b: Double, y: Double): Double = {
      level match {
        case _ if level < submitPhotoResults => 0
        case _ if (level < submitPhotoQuests) && (level >= submitPhotoResults) => megaf(level, k, d, b, y)
        case _ => coinSelectQuestInt(inviteFriends - 1, k, d, b, y) * 0.63 + megaf(level, k, d, b, y) * 0.37
      }
    }
    
    coinSelectQuestInt(level, k, d, b, y)
  }
  
}
