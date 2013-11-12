package logic.internal

import logic.constants._
import basefunctions._

private [logic] object spendcoinsfunctions {
  /**
   * How much coins per day player should spend on shuffling
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

}