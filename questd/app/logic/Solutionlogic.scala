package logic

import controllers.domain.DomainAPIComponent
import models.domain.solution.Solution


class SolutionLogic(
  val qs: Solution,
  val api: DomainAPIComponent#DomainAPI) {

  /**
   * Should we ban quest for cheating and stop displaying it to everyone.
   */
  def shouldBanCheating = {
    val votesToThreatAsCheating = Math.max(
      api.config(api.DefaultConfigParams.SolutionCheatingRatio).toDouble * qs.rating.votersCount,
      api.config(api.DefaultConfigParams.SolutionMinCheatingVotes).toLong)

    qs.rating.cheating > votesToThreatAsCheating
  }

  /**
   * Should we ban it for IAC
   */
  def shouldBanIAC = {
    val pointsToBan = Math.max(
      api.config(api.DefaultConfigParams.SolutionIACRatio).toDouble * qs.rating.votersCount,
      api.config(api.DefaultConfigParams.SolutionMinIACVotes).toLong)

    val maxPoints = List(
      qs.rating.iacpoints.porn,
      qs.rating.iacpoints.spam).max

    maxPoints > pointsToBan
  }

}

