package logic

import controllers.domain.DomainAPIComponent
import logic.constants._
import logic.functions._
import models.domain.common.Assets
import models.domain.quest.Quest
import play.Logger

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
    Logger.trace(s"dailyIncomeForLikes, quest ${quest.id} / likes ${quest.rating.likesCount} / coins = ${dailyQuestIncomeForLikes(quest.rating.likesCount)}")

    Assets(coins = dailyQuestIncomeForLikes(quest.rating.likesCount))
  }

  /**
   * Gives reward per quest solving for the quest.
   * @return Reward for each quest solving
   */
  def rewardForSolution: Assets = {
    Assets(coins = questIncomeForSolving)
  }

  /**
   * Penalty for cheating solution
   */
  def penaltyForCheatingSolution = {
    QuestLogic.rewardForSolvingQuest(quest.info.level, api) * QuestSolutionCheatingPenalty
  }

  /**
   * Penalty for IAC solution
   */
  def penaltyForIACSolution = {
    QuestLogic.rewardForSolvingQuest(quest.info.level, api) * QuestSolutionIACPenalty
  }

  /**
   * Should we ban quest.
   */
  def shouldBanIAC = {
    val votesToBan = Math.max(
        api.config(api.DefaultConfigParams.QuestIACRatio).toDouble * quest.rating.votersCount,
        api.config(api.DefaultConfigParams.QuestMinIACVotes).toLong)

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
      api.config(api.DefaultConfigParams.QuestCheatingRatio).toDouble * quest.rating.votersCount,
      api.config(api.DefaultConfigParams.QuestMinCheatingVotes).toLong)

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
   * Reward for won battle.
   */
  def rewardForWinningBattle(questLevel: Int, api: DomainAPIComponent#DomainAPI) = {
    Assets(rating = ratingToWinQuest(questLevel)) * api.config(api.DefaultConfigParams.DebugExpMultiplier).toDouble
  }

  /**
   * Reward for lost battle.
   */
  def rewardForLosingBattle(questLevel: Int, api: DomainAPIComponent#DomainAPI) = {
    Assets(rating = ratingToLoseQuest(questLevel)) * api.config(api.DefaultConfigParams.DebugExpMultiplier).toDouble
  }

  /**
   * Reward for solving quest.
   */
  def rewardForSolvingQuest(questLevel: Int, api: DomainAPIComponent#DomainAPI) = {
    Assets(rating = ratingToSolveQuest(questLevel)) * api.config(api.DefaultConfigParams.DebugExpMultiplier).toDouble
  }

}
