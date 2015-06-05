package models.domain.user

/**
 * Status of friendship connection.
 */
object ReferralStatus extends Enumeration {
  val Refers, ReferredBy, None = Value
}
