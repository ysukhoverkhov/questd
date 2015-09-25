package models.domain.challenge

/**
 * Status of battle request.
 */
object ChallengeStatus extends Enumeration {
  val Requested, Accepted, Rejected, AutoCreated = Value
}
