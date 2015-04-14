package models.domain


object FriendshipStatus extends Enumeration {
  val Invited, Invites, Accepted = Value
}

/**
 * Represents friendship status
 */
case class Friendship(
  friendId: String,
  status: FriendshipStatus.Value)

