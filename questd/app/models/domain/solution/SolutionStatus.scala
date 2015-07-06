package models.domain.solution

/**
 * Status of a solution.
 */
object SolutionStatus extends Enumeration {
  val InRotation, CheatingBanned, IACBanned, OldBanned, AdminBanned, ForTutorial = Value
}
