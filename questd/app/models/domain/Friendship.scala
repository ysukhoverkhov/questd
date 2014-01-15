package models.domain


object FriendshipStatus extends Enumeration {
  val Invited, Invites, Accepted, Rejected, Divorsed = Value
}


/**
 * Represents friendship status
 */
case class Friendship(
  val friendId: String,
  val status: String)
    
