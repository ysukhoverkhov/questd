package logic

import logic.functions._
import logic.constants._
import models.domain._
import controllers.domain.DomainAPIComponent

class QuestLogic(
  val quest: Quest,
  val api: DomainAPIComponent#DomainAPI) {

  /**
   * Get cost of solving the quest.
   */
  def costOfSolving: Assets = {
    QuestLogic.costOfSolvingQuest(quest.info.level)
  }

  /**
   * Passive income a quest generates per day.
   * @return Passive income.
   */
  def dailyPassiveIncome: Assets = {
    Assets(coins = dailyQuestPassiveIncome)
  }

  /**
   * Calculates income we receive for quest likes.
   * @return Income for likes.
   */
  def dailyIncomeForLikes: Assets = {
    Assets(coins = dailyQuestIncomeForLikes(quest.rating.likesCount))
  }

  /**
   * Penalty for cheating solution
   */
  def penaltyForCheatingSolution = {
    QuestLogic.rewardForLosingQuest(quest.info.level, api) * QuestSolutionCheatingPenalty
  }

  /**
   * Penalty for IAC solution
   */
  def penaltyForIACSolution = {
    QuestLogic.rewardForLosingQuest(quest.info.level, api) * QuestSolutionIACPenalty
  }

  /**
   * Should we ban quest.
   */
  def shouldBanIAC = {
    val votesToBan = Math.max(
        api.config(api.ConfigParams.ProposalIACRatio).toDouble * quest.rating.votersCount,
        api.config(api.ConfigParams.ProposalMinIACVotes).toLong)

    val maxVotes = List(
        quest.rating.iacpoints.porn,
        quest.rating.iacpoints.spam).max
    maxVotes > votesToBan
  }

  /**
   * Should we decide user is a cheater.
   */
  def shouldBanCheating = {

    val maxCheatingVotes = Math.max(
      api.config(api.ConfigParams.ProposalCheatingRatio).toDouble * quest.rating.votersCount,
      api.config(api.ConfigParams.ProposalMinCheatingVotes).toLong)

    quest.rating.cheating > maxCheatingVotes
  }
}

object QuestLogic {
  /**
   * Get cost of solving the quest.
   */
  def costOfSolvingQuest(questLevel: Int) = {
    Assets(coins = coinSelectQuest(questLevel))
  }

  /**
   * Reward for lost quest.
   */
  def rewardForLosingQuest(questLevel: Int, api: DomainAPIComponent#DomainAPI) = {
    Assets(rating = ratingToLoseQuest(questLevel)) * api.config(api.ConfigParams.DebugExpMultiplier).toDouble
  }

  /**
   * Reward for won quest.
   */
  def rewardForWinningQuest(questLevel: Int, api: DomainAPIComponent#DomainAPI) = {
    Assets(rating = ratingToWinQuest(questLevel)) * api.config(api.ConfigParams.DebugExpMultiplier).toDouble
  }

}
