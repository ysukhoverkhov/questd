package logic

import components.componentregistry.ComponentRegistrySingleton
import play.Logger
import models.domain._
import controllers.domain.app.user._
import controllers.domain.OkApiResult
import controllers.domain.DomainAPIComponent
import java.util.Date

class QuestSolutionLogic(
  val qs: QuestSolution,
  val api: DomainAPIComponent#DomainAPI) {

  /**
   * We check is time come to stop voting for the solution.
   */
  def shouldStopVoting = {
    (qs.status == QuestSolutionStatus.OnVoting.toString) && ((new Date()).after(qs.voteEndDate))
  }

  // TODO: apply min votes count restriction. perhaps something like 3 iacs.
  def shouldBanCheating = {
    (qs.status == QuestSolutionStatus.OnVoting.toString) && {
      val votesToThreatAsCheating = api.config(api.ConfigParams.SolutionCheatingRatio).toDouble * qs.rating.reviewsCount
      qs.rating.cheating > votesToThreatAsCheating
    }
  }

  // TODO: apply min votes count restriction. perhaps something like 3 iacs.
  def shouldBanIAC = {
    val pointsToBan = api.config(api.ConfigParams.SolutionIACRatio).toDouble * qs.rating.reviewsCount

    ((qs.rating.iacpoints.porn > pointsToBan)
      || (qs.rating.iacpoints.spam > pointsToBan))
  }

  /**
   * Calculate points for quest solution voting.
   */
  def calculatePoints = {
    List(
        qs.rating.pointsRandom, 
        qs.rating.pointsFriends * constants.friendsVoteMult,
        qs.rating.pointsInvited * constants.invitedVoteMult).sum
  }
}

