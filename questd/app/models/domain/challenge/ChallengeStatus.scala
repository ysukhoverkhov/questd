package models.domain.challenge

/**
 * Status of battle request.
 */
object ChallengeStatus extends Enumeration {
  val Requested, Requests, Accepted, Rejected, AutoCreated = Value
}
