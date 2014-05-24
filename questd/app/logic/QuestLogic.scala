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
  def shouldBanQuest = {
    val maxIACVotes = api.config(api.ConfigParams.ProposalIACRatio).toDouble * api.config(api.ConfigParams.ProposalVotesToLeaveVoting).toLong
    if ((quest.rating.iacpoints.porn > maxIACVotes)
      || (quest.rating.iacpoints.spam > maxIACVotes))
      true
    else
      false
  }

  def shouldCheatingQuest = {
    val maxCheatingVotes = api.config(api.ConfigParams.ProposalCheatingRatio).toDouble * api.config(api.ConfigParams.ProposalVotesToLeaveVoting).toLong
    if ((quest.rating.cheating > maxCheatingVotes) && (QuestStatus.withName(quest.status) == QuestStatus.OnVoting))
      true else false
  }

  def shouldRemoveQuestFromVotingByTime = {
    if ((quest.rating.votersCount > api.config(api.ConfigParams.ProposalVotesToLeaveVoting).toLong) &&
        ((quest.rating.points / quest.rating.votersCount) < api.config(api.ConfigParams.ProposalRatioToLeaveVoting).toDouble) &&
        (QuestStatus.withName(quest.status) == QuestStatus.OnVoting))
      true
    else
      false
  }
}

