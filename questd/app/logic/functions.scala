package logic

import internal.gainratingfunctions._
import internal.spendcoinsfunctions._


object functions {

  import constants._

  /**************************
   * Proposing quests.
   **************************/
  
  /**
   * Rating to give user for successful (approved) proposal at a level.
   * // TODO write test for me.
   */
  def ratingForProposalAtLevel(level: Int): Int = {
    val proposalPeriodAtMaxLevel = 2

    (proposalPeriodAtMaxLevel * ratingForSubmitProposal(level) * math.pow(maxLevel.toDouble / level, 3)).toInt
  }

  /**
   * Period in days to give players a task to make quest.
   * // TODO write test for me.
   */
  def questProposalPeriod(level: Int): Int = math.round(ratingForProposalAtLevel(level).toFloat / ratingForSubmitProposal(level).toFloat)

  /**
   * Cost to skip a single proposal
   * // TODO write test for me.
   */
  def costToSkipProposal(level: Int, skipNumber: Int): Int = {
    
    def costToSkipProposalInt(level: Int, skipNumber: Int, k: Double) = {
      k * math.pow(5.0 / 3.0, skipNumber)
    }
    
    def kf(level: Int) = {
      coinsShuffleTheme(level) / (1 to numberOfThemesSkipsForCoins).map(x => costToSkipProposalInt(level, x, 1)).sum
    }
    
    math.round(costToSkipProposalInt(level, skipNumber, kf(level)) * questProposalPeriod(level)).toInt
  }
  
  /**
   * Cost to propose a single quest.
   * // TODO write test for me.
   */
  def costToProposeQuest(level: Int): Int = {
    math.round(coinProposeQuest(level)  * questProposalPeriod(level)).toInt
  }
  
  /**
   * Cost to give up quest proposal.
   * // TODO write test for me.
   */
  def ratingToGiveUpQuestProposal(level: Int): Int = {
    math.round(ratingForSubmitProposal(level) * questProposalPeriod(level) * questProposalGiveUpPenalty).toInt
  }
  
  
  /***********************
   * Purchasing of quests.
   ***********************/
  
  /**
   * Cost to skip a single proposal
   * // TODO write test for me.
   */
  def costToSkipQuest(level: Int, skipNumber: Int, currentQuestDuration: Int): Int = {
    
    def costToSkipQuestInt(level: Int, skipNumber: Int, k: Double) = {
      k * math.pow(4.0 / 3.0, skipNumber)
    }
    
    def kf(level: Int) = {
      coinShuffleQuest(level) / (1 to numberOfQuestsSkipsForCoins).map(x => costToSkipQuestInt(level, x, 1)).sum
    }
    
    math.round(costToSkipQuestInt(level, skipNumber, kf(level)) * currentQuestDuration).toInt
  }
  
}
