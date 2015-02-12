package logic.user

import logic._
import logic.functions._
import controllers.domain.app.protocol.ProfileModificationResult._
import models.domain._

/**
 * All friends related logic.
 */
trait Friends { this: UserLogic =>

  def canFollow = {
    if (!user.profile.rights.unlockedFunctionality.contains(Functionality.AddToFollowing))
      NotEnoughRights
    else if (!(user.profile.assets canAfford costToFollowing))
      NotEnoughAssets
    else
      OK
  }

  def costToFollowing = {
    Assets(coins = costToFollowPerson(user.profile.publicProfile.level))
  }

  def canAddFriend(potentialFriend: User) = {
    if (!user.profile.rights.unlockedFunctionality.contains(Functionality.InviteFriends))
      NotEnoughRights
    else if (!(user.profile.assets canAfford costToAddFriend(potentialFriend)))
      NotEnoughAssets
    else if (user.friends.length >= user.profile.rights.maxFriendsCount)
      LimitExceeded
    else if (user.profile.publicProfile.level < potentialFriend.profile.publicProfile.level)
      NotEnoughRights
    else
      OK
  }

  def costToAddFriend(potentialFriend: User) = {
    Assets(coins = costToInviteFriend(potentialFriend.profile.publicProfile.level))
  }

}
