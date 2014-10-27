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
   * Are we able to add quest to rotation.
   */
  // TODO: clean me up.
//  def shouldAddToRotation = {
//    if ((quest.rating.points > api.config(api.ConfigParams.ProposalLikesToEnterRotation).toLong) && (quest.status == QuestStatus.OnVoting))
//      true
//    else
//      false
//  }

  /**
   * Should we remove quest from rotation.
   */
  // TODO: clean me up.
//  def shouldRemoveFromRotation = {
//    if ((quest.rating.points < api.config(api.ConfigParams.ProposalLikesToEnterRotation).toLong / 2) && (quest.status == QuestStatus.InRotation))
//      true
//    else
//      false
//  }

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
  // TODO: clean me up.
  def shouldBanCheating = {
    val maxCheatingVotes = api.config(api.ConfigParams.ProposalCheatingRatio).toDouble * api.config(api.ConfigParams.ProposalVotesToLeaveVoting).toLong
    quest.rating.cheating > maxCheatingVotes // && (quest.status == QuestStatus.OnVoting)
  }

  /**
   * Should we remove it because it's with us for too long without a reason.
   */
  // TODO: clean me up.
//  def shouldRemoveQuestFromVotingByTime = {
//    (quest.rating.votersCount > api.config(api.ConfigParams.ProposalVotesToLeaveVoting).toLong) &&
//      ((quest.rating.points.toDouble / quest.rating.votersCount.toDouble) < api.config(api.ConfigParams.ProposalRatioToLeaveVoting).toDouble) &&
//      (quest.status == QuestStatus.OnVoting)
//  }
}

object QuestLogic {
  /**
   * Get cost of solving the quest.
   */
  def costOfSolvingQuest(questLevel: Int) = {
    Assets(coins = coinSelectQuest(questLevel))
  }
}
