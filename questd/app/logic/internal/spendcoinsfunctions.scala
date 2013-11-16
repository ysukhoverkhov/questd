package logic.internal

import logic.constants._
import basefunctions._

private [logic] object spendcoinsfunctions {
 
  /**
   * How much coins per day player should spend on shuffling themes for quest proposals.
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
   * How much coins per day we should spend on proposing quests per day at level.
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
  
}
