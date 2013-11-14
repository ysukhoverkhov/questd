package logic

import internal.gainratingfunctions._
import internal.spendcoinsfunctions._


object functions {

  import constants._

  /**************************
   * Proposing quests.
   **************************/
  
  /**
   * Number of themes skips for coins.
   */
  def numberOfThemesSkipsForCoins = 4

  /**
   * Rating to give user for successful (approved) proposal at a level.
   */
  def ratingForProposalAtLevel(level: Int): Int = {
    val proposalPeriodAtMaxLevel = 2

    (proposalPeriodAtMaxLevel * ratingForSubmitProposal(level) * math.pow(maxLevel.toDouble / level, 3)).toInt
  }

  /**
   * Period in days to give players a task to make quest.
   */
  def questProposalPeriod(level: Int): Int = math.round(ratingForProposalAtLevel(level).toFloat / ratingForSubmitProposal(level).toFloat)

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
  
  
}
