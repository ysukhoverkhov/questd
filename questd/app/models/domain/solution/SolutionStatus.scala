package models.domain.solution

/**
 * Status of a solution.
 */
object SolutionStatus extends Enumeration {
  val InRotation, ForTutorial, CheatingBanned, IACBanned, OldBanned, AdminBanned, AuthorBanned = Value
}
