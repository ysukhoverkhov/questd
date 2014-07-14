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
    val l: Int = (quest.rating.difficultyRating.easy * constants.easyWeight
      + quest.rating.difficultyRating.normal * constants.normalWeight
      + quest.rating.difficultyRating.hard * constants.hardWeight
      + quest.rating.difficultyRating.extreme * constants.extremeWeight) / totalVotes

    math.min(constants.maxQuestLevel, math.max(constants.minQuestLevel, l))
  }

  /**
   * Calculate difficulty of a quest.
   */
  def calculateDifficulty = {
    List(
      (QuestDifficulty.Easy, quest.rating.difficultyRating.easy),
      (QuestDifficulty.Normal, quest.rating.difficultyRating.normal),
      (QuestDifficulty.Hard, quest.rating.difficultyRating.hard),
      (QuestDifficulty.Extreme, quest.rating.difficultyRating.extreme)).reduce((l, r) => if (l._2 > r._2) l else r)
  }

  /**
   * Calculate duration of a quest.
   */
  def calculateDuration = {
    List(
      (QuestDuration.Minutes, quest.rating.durationRating.mins),
      (QuestDuration.Hour, quest.rating.durationRating.hour),
      (QuestDuration.Day, quest.rating.durationRating.day),
      (QuestDuration.Week, quest.rating.durationRating.week)).reduce((l, r) => if (l._2 > r._2) l else r)
  }

  /**
   * Are we able to add quest to rotation.
   */
  def shouldAddToRotation = {
    if ((quest.rating.points > api.config(api.ConfigParams.ProposalLikesToEnterRotation).toLong) && (QuestStatus.withName(quest.status) == QuestStatus.OnVoting))
      true
    else
      false
  }

  /**
   * Should we remove quest from rotation.
   */
  def shouldRemoveFromRotation = {
    if ((quest.rating.points < api.config(api.ConfigParams.ProposalLikesToEnterRotation).toLong / 2) && (QuestStatus.withName(quest.status) == QuestStatus.InRotation))
      true
    else
      false
  }

  /**
   * Should we ban quest.
   */
  def shouldBanIAC = {
    val votesToBan = api.config(api.ConfigParams.ProposalIACRatio).toDouble * api.config(api.ConfigParams.ProposalVotesToLeaveVoting).toLong
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
    ((quest.rating.cheating > maxCheatingVotes) && (QuestStatus.withName(quest.status) == QuestStatus.OnVoting))
  }

  /**
   * Should we remove it because it's with us for too long without a reason.
   */
  def shouldRemoveQuestFromVotingByTime = {
    if ((quest.rating.votersCount > api.config(api.ConfigParams.ProposalVotesToLeaveVoting).toLong) &&
      ((quest.rating.points.toDouble / quest.rating.votersCount.toDouble) < api.config(api.ConfigParams.ProposalRatioToLeaveVoting).toDouble) &&
      (QuestStatus.withName(quest.status) == QuestStatus.OnVoting))
      true
    else
      false
  }
}

