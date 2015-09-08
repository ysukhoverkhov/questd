package logic

import controllers.domain.DomainAPIComponent
import models.domain.solution.Solution


class SolutionLogic(
  val solution: Solution,
  val api: DomainAPIComponent#DomainAPI) {

  /**
   * Should we ban quest for cheating and stop displaying it to everyone.
   */
  def shouldBanCheating = {
    val votesToThreatAsCheating = Math.max(
      api.config(api.DefaultConfigParams.SolutionCheatingRatio).toDouble * solution.rating.votersCount,
      api.config(api.DefaultConfigParams.SolutionMinCheatingVotes).toLong)

    solution.rating.cheating > votesToThreatAsCheating
  }

  /**
   * Should we ban it for IAC
   */
  def shouldBanIAC = {
    val pointsToBan = Math.max(
      api.config(api.DefaultConfigParams.SolutionIACRatio).toDouble * solution.rating.votersCount,
      api.config(api.DefaultConfigParams.SolutionMinIACVotes).toLong)

    val maxPoints = List(
      solution.rating.iacpoints.porn,
      solution.rating.iacpoints.spam).max

    maxPoints > pointsToBan
  }

  /**
   * Is the solution can be used in autobattles.
   */
  def canParticipateAutoBattle = {
    solution.battleIds.isEmpty
  }
}

