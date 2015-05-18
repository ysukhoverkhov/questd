package models.domain.user

/**
 * Status of friendship connection.
 */
object FriendshipStatus extends Enumeration {
  val Invited, Invites, Accepted = Value
}
