package logic.internal

import logic.internal.basefunctions._

object gainratingfunctions {

  /**
   * How much rating per day we should receive from submitting quests.
   */
  def ratingForSubmitResult(level: Int): Double = {
    val k = 152.311546
    val d = 5.171203
    val b = -78.096818
    val y = -11.309628

    def ratingForSubmitResultInt(level: Int, k: Double, d: Double, b: Double, y: Double): Double = {
      megaf(level, k, d, b, y)
    }

    ratingForSubmitResultInt(level, k, d, b, y)
  }
}
