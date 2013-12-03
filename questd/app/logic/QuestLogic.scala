package logic


import components.componentregistry.ComponentRegistrySingleton
import play.Logger
import models.domain._

class QuestLogic(val quest: Quest) {

  lazy val api = ComponentRegistrySingleton.api

  /**
   * Check should quest change its status or should not.
   */
  def updateStatus: Quest = {
    {
      checkAddToRotation.checkRemoveFromRotation.checkBanQuest.checkCheatingQuest.checkRemoveQuestFromVotingByTime.capPoints
    }.quest
  }

  private def checkAddToRotation: QuestLogic = {
    if ((quest.rating.points > pointsToAddQuestToRotation) && (QuestStatus.withName(quest.status) == QuestStatus.OnVoting))
      new QuestLogic(quest.copy(status = QuestStatus.InRotation.toString))
    else
      this
  }
  
  private def checkRemoveFromRotation: QuestLogic = {
    if ((quest.rating.points < pointsToAddQuestToRotation / 2) && (QuestStatus.withName(quest.status) == QuestStatus.InRotation))
      new QuestLogic(quest.copy(status = QuestStatus.RatingBanned.toString))
    else
      this
  }
  
  private def checkBanQuest: QuestLogic = {
    if ((quest.rating.iacpoints.porn.toFloat / quest.rating.votersCount > iacBanRatio) 
        || (quest.rating.iacpoints.spam.toFloat / quest.rating.votersCount > iacBanRatio))
      new QuestLogic(quest.copy(status = QuestStatus.IACBanned.toString))
    else
      this
  }
  
  private def checkCheatingQuest: QuestLogic = {
    if ((quest.rating.cheating > pointsToAddQuestToRotation) && (QuestStatus.withName(quest.status) == QuestStatus.OnVoting))
      new QuestLogic(quest.copy(status = QuestStatus.CheatingBanned.toString))
    else
      this
  }
  
  private def checkRemoveQuestFromVotingByTime: QuestLogic = {
    if ((quest.rating.votersCount > votersToRemoveQuestFromVoting) && (QuestStatus.withName(quest.status) == QuestStatus.OnVoting))
      new QuestLogic(quest.copy(status = QuestStatus.OldBanned.toString))
    else
      this
  }

  private def capPoints: QuestLogic = {
    if (quest.rating.points > Int.MaxValue / 2)
      new QuestLogic(quest.copy(rating = quest.rating.copy(points = quest.rating.points / 2)))
    else
      this
  }

  private def votersToRemoveQuestFromVoting = 100
  private def pointsToAddQuestToRotation = 10
  private def iacBanRatio = 0.1
}

