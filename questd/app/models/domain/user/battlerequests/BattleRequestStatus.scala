package models.domain.user.battlerequests

/**
 * Status of battle request.
 */
object BattleRequestStatus extends Enumeration {
  val Requested, Requests, Accepted, Rejected = Value
}
