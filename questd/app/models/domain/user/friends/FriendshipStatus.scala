package models.domain.user.friends

/**
 * Status of friendship connection.
 */
object FriendshipStatus extends Enumeration {
  val Invited, Invites, Accepted = Value
}
