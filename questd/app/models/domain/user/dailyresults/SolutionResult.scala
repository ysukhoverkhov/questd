package models.domain.user.dailyresults

import models.domain.common.Assets
import models.domain.solution.SolutionStatus

/**
 * Result of solution creation.
 */
case class SolutionResult(
  solutionId: String,
  reward: Assets,
  status: SolutionStatus.Value)
