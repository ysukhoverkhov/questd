package logic

import models.domain._
import controllers.domain.DomainAPIComponent
import java.util.Date

class QuestSolutionLogic(
  val qs: QuestSolution,
  val api: DomainAPIComponent#DomainAPI) {

  /**
   * We check is time come to stop voting for the solution.
   */
  def shouldStopVoting = {
    (qs.status == QuestSolutionStatus.OnVoting) && new Date().after(qs.voteEndDate)
  }

  /**
   * Should we ban quest for cheating and stop displaying it to everyone.
   */
  def shouldBanCheating = {
    val votesToThreatAsCheating = Math.max(
      api.config(api.ConfigParams.SolutionCheatingRatio).toDouble * qs.rating.reviewsCount,
      api.config(api.ConfigParams.SolutionMinCheatingVotes).toLong)

    qs.rating.cheating > votesToThreatAsCheating
  }

  /**
   * Should we ban it for IAC
   */
  def shouldBanIAC = {
    val pointsToBan = Math.max(
      api.config(api.ConfigParams.SolutionIACRatio).toDouble * qs.rating.reviewsCount,
      api.config(api.ConfigParams.SolutionMinIACVotes).toLong)

    val maxPoints = List(
      qs.rating.iacpoints.porn,
      qs.rating.iacpoints.spam).max

    maxPoints > pointsToBan
  }

  /**
   * Calculate points for quest solution voting.
   */
  def calculatePoints = {
    List(
      qs.rating.pointsRandom,
      qs.rating.pointsFriends * constants.FriendsVoteMult).sum
  }
}

