package models.domain.user

/**
 * Represents friendship status
 */
case class Friendship(
  friendId: String,
  status: FriendshipStatus.Value)
