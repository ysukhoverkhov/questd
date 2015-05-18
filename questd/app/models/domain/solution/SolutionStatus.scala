package models.domain.solution

/**
 * Status of a solution.
 */
object SolutionStatus extends Enumeration {
  val WaitingForCompetitor, OnVoting, Won, Lost, CheatingBanned, IACBanned = Value
}
