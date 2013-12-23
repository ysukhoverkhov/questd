package logic.internal

import logic.constants._
import basefunctions._

object spendratingfunctions {
 
  /**
   * How much coins per day player should spend on shuffling themes for quest proposals.
   */
  def ratDecrease(level: Int): Double = {
    val k = -426.439
    val d = 8.002
    val b = 190.761 
    val y = 18.34
    
    if (level < submitPhotoResults)
      0
    else
      -megaf(level, k, d, b, y)
  }

}
