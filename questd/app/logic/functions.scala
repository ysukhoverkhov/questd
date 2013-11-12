package logic

object functions {

  import constants._

  /**
   * Number of themes skips for coins.
   */
  def numberOfThemesSkipsForCoins = 4

  /**
   * Rating to give user for successful (approved) proposal at a level.
   */
  def ratingForProposalAtLevel(level: Int) = {
    val proposalPeriodAtMaxLevel = 2

    proposalPeriodAtMaxLevel * ratingForSubmitProposal(level) * math.pow(maxLevel.toDouble / level, 3)
  }

  /**
   * How much coins per day player should spend on shuffling
   */
  // TODO implement me
  private def coinsShuffleTheme(level: Int): Double = 200
  
  /**
   * How much rating per day we should receive from submitting proposals.
   */
  private def ratingForSubmitProposal(level: Int) = {
    val k = 504.47957
    val d = 7.83618
    val b = -1702.1146
    val y = -9.08269
    
    if (level < submitPhotoQuests)
      0
    else
      megaf(level, k, d, b, y)
  }

  /**
   * Period in days to give players a task to make quest.
   */
  def questProposalPeriod(level: Int) = math.round(ratingForProposalAtLevel(level) / ratingForSubmitProposal(level))

  /**
   * Cost to skip a single proposal
   */
  def costToSkipProposal(level: Int, skipNumber: Int): Int = {
    
    def costToSkipProposalInt(level: Int, skipNumber: Int, k: Double) = {
      k * math.pow(5.0 / 3.0, skipNumber)
    }
    
    def kf(level: Int) = {
      coinsShuffleTheme(level) / (1 to numberOfThemesSkipsForCoins).map(x => costToSkipProposalInt(level, x, 1)).sum
    }
    
    math.round(costToSkipProposalInt(level, skipNumber, kf(level))).toInt
  }
  
  
  
  
  
  /**
   * Our super mega function what rules all curves
   */
  private def megaf(level: Int, k: Double, d: Double, b: Double, y: Double) = k * math.exp((level - 1) / d) + y * level + b
}
