package logic

import logic.functions._
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
}
