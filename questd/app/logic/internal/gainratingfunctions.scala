package logic.internal

import logic.constants._
import basefunctions._
import models.domain.Functionality._

object gainratingfunctions {
  
  /**
   * How much rating per day we should receive from submitting proposals.
   */
  def ratingForSubmitProposal(level: Int): Double = {
    val k = 504.47957
    val d = 7.83618
    val b = -1702.1146
    val y = -9.08269
    
    if (level < levelFor(SubmitPhotoQuests))
      0
    else
      megaf(level, k, d, b, y)
  }

  /**
   * How much rating per day we should receive from submitting quests.
   */
  def ratingForSubmitResult(level: Int): Double = {
    val k = 6881.2529
    val d = 18.5365
    val b = -5953.6977
    val y = -322.5152
    
    def ratingForSubmitResultInt(level: Int, k: Double, d: Double, b: Double, y: Double): Double = {
      level match {
        case _ if level < levelFor(SubmitPhotoResults) => 0
        case _ if (level < levelFor(SubmitPhotoQuests)) && (level >= levelFor(SubmitPhotoResults)) => megaf(level, k, d, b, y)
        case _ => ratingForSubmitResultInt(levelFor(SubmitPhotoQuests) - 1, k, d, b, y) * 0.68 + megaf(level, k, d, b, y) * 0.32
      }
    }
    
    ratingForSubmitResultInt(level, k, d, b, y)
  }
}
