package logic.internal

import logic.constants._
import basefunctions._

private [logic] object gainratingfunctions {
  
  /**
   * How much rating per day we should receive from submitting proposals.
   */
  def ratingForSubmitProposal(level: Int): Double = {
    val k = 504.47957
    val d = 7.83618
    val b = -1702.1146
    val y = -9.08269
    
    if (level < submitPhotoQuests)
      0
    else
      megaf(level, k, d, b, y)
  }

}
