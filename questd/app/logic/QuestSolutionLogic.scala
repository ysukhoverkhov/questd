package logic

import components.componentregistry.ComponentRegistrySingleton
import play.Logger
import models.domain._
import controllers.domain.app.user._
import controllers.domain.OkApiResult
import controllers.domain.DomainAPIComponent

class QuestSolutionLogic(
    val qs: QuestSolution, 
    val api: DomainAPIComponent#DomainAPI) {

  def shouldStopVoting = {
    (qs.status == QuestSolutionStatus.OnVoting.toString) && (qs.rating.reviewsCount >= reviewsToFinishVoting)
  }

  def shouldBanCheating = {
    (qs.status == QuestSolutionStatus.OnVoting.toString) && (qs.rating.cheating >= cheatingToThreatAsCheating)
  }

  def shouldBanIAC = {
    ((qs.rating.iacpoints.porn.toFloat / qs.rating.reviewsCount > iacBanRatio)
      || (qs.rating.iacpoints.spam.toFloat / qs.rating.reviewsCount > iacBanRatio))
  }
  
  /**
   * Calculate points for quest solution voting.
   */
  def calculatePoints = {
    qs.rating.pointsRandom + qs.rating.pointsFriends * constants.FriendsVoteMult + qs.rating.pointsInvited * constants.InvitedVoteMult
  }

  private def reviewsToFinishVoting = 3
  private def cheatingToThreatAsCheating = 3
  private def iacBanRatio = 0.1
}

