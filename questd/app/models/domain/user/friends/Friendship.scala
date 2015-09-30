package models.domain.user.friends

import java.util.Date

/**
 * Represents friendship status
 */
case class Friendship(
  friendId: String,
  status: FriendshipStatus.Value,
  referralStatus: ReferralStatus.Value = ReferralStatus.None,
  referredWithContentId: Option[String] = None,
  creationDate: Date = new Date())
