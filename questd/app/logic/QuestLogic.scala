package logic

import components.componentregistry.ComponentRegistrySingleton
import play.Logger
import models.domain._
import controllers.domain.app.user._
import controllers.domain.OkApiResult
import models.domain.admin.ConfigSection
import controllers.domain.config.ApiConfigHolder
import controllers.domain.DomainAPIComponent

class QuestLogic(
  val quest: Quest,
  val api: DomainAPIComponent#DomainAPI) {

  /**
   * Calculate level of a quest with current votes.
   */
  def calculateQuestLevel = {
    val totalVotes = quest.rating.difficultyRating.easy + quest.rating.difficultyRating.normal + quest.rating.difficultyRating.hard + quest.rating.difficultyRating.extreme
    val l: Int = (quest.rating.difficultyRating.easy * constants.EasyWeight 
        + quest.rating.difficultyRating.normal * constants.NormalWeight 
        + quest.rating.difficultyRating.hard * constants.HardWeight 
        + quest.rating.difficultyRating.extreme * constants.ExtremeWeight) / totalVotes

    math.min(constants.MaxQuestLevel, math.max(constants.MinQuestLevel, l))
  }

  /**
   * Calculate difficulty of a quest.
   */
  def calculateDifficulty: QuestDifficulty.Value = {
    List(
      (QuestDifficulty.Easy, quest.rating.difficultyRating.easy),
      (QuestDifficulty.Normal, quest.rating.difficultyRating.normal),
      (QuestDifficulty.Hard, quest.rating.difficultyRating.hard),
      (QuestDifficulty.Extreme, quest.rating.difficultyRating.extreme)).reduce((l, r) => if (l._2 > r._2) l else r)._1
  }

  /**
   * Calculate duration of a quest.
   */
  def calculateDuration: QuestDuration.Value = {
    List(
      (QuestDuration.Minutes, quest.rating.durationRating.mins),
      (QuestDuration.Hour, quest.rating.durationRating.hour),
      (QuestDuration.Day, quest.rating.durationRating.day),
      (QuestDuration.Week, quest.rating.durationRating.week)).reduce((l, r) => if (l._2 > r._2) l else r)._1
  }

  /**
   * Are we able to add quest to rotation.
   */
  def shouldAddToRotation = {
    if ((quest.rating.points > api.config(api.ConfigParams.ProposalLikesToEnterRotation).toLong) && (quest.status == QuestStatus.OnVoting))
      true
    else
      false
  }

  /**
   * Should we remove quest from rotation.
   */
  def shouldRemoveFromRotation = {
    if ((quest.rating.points < api.config(api.ConfigParams.ProposalLikesToEnterRotation).toLong / 2) && (quest.status == QuestStatus.InRotation))
      true
    else
      false
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
    val maxCheatingVotes = api.config(api.ConfigParams.ProposalCheatingRatio).toDouble * api.config(api.ConfigParams.ProposalVotesToLeaveVoting).toLong
    ((quest.rating.cheating > maxCheatingVotes) && (quest.status == QuestStatus.OnVoting))
  }

  /**
   * Should we remove it because it's with us for too long without a reason.
   */
  def shouldRemoveQuestFromVotingByTime = {
    ((quest.rating.votersCount > api.config(api.ConfigParams.ProposalVotesToLeaveVoting).toLong) &&
      ((quest.rating.points.toDouble / quest.rating.votersCount.toDouble) < api.config(api.ConfigParams.ProposalRatioToLeaveVoting).toDouble) &&
        (quest.status == QuestStatus.OnVoting))
  }
}

