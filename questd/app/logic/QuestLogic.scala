package logic

import components.componentregistry.ComponentRegistrySingleton
import play.Logger
import models.domain._
import controllers.domain.app.user._
import controllers.domain.OkApiResult

class QuestLogic(val quest: Quest) {

  lazy val api = ComponentRegistrySingleton.api


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
   * Are we able to add quest to rotation.
   */
  def shouldAddToRotation = {
    if ((quest.rating.points > pointsToAddQuestToRotation) && (QuestStatus.withName(quest.status) == QuestStatus.OnVoting))
      true
    else
      false
  }

  /**
   * Should we remove quest from rotation.
   */
  def shouldRemoveFromRotation = {
    if ((quest.rating.points < pointsToAddQuestToRotation / 2) && (QuestStatus.withName(quest.status) == QuestStatus.InRotation))
      true
    else
      false
  }

  /**
   * Should we ban quest.
   */
  def shouldBanQuest = {
    if ((quest.rating.iacpoints.porn.toFloat / quest.rating.votersCount > iacBanRatio)
      || (quest.rating.iacpoints.spam.toFloat / quest.rating.votersCount > iacBanRatio))
      true
    else
      false
  }

  def shouldCheatingQuest = {
    if ((quest.rating.cheating > pointsToAddQuestToRotation) && (QuestStatus.withName(quest.status) == QuestStatus.OnVoting))
      true
    else
      false
  }

  def shouldRemoveQuestFromVotingByTime = {
    if ((quest.rating.votersCount > votersToRemoveQuestFromVoting) && (QuestStatus.withName(quest.status) == QuestStatus.OnVoting))
      true
    else
      false
  }


  private def votersToRemoveQuestFromVoting = 100
  private def pointsToAddQuestToRotation = 10
  private def iacBanRatio = 0.1
}

