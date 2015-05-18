package models.domain.user

import models.domain.common.Assets
import models.domain.solution.SolutionStatus

/**
 * result of solution creation.
 */
case class SolutionResult(
    solutionId: String,
    battleId: Option[String] = None,
    reward: Option[Assets],
    penalty: Option[Assets],
    status: SolutionStatus.Value)
