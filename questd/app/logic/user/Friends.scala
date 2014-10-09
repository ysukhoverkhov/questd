package logic.user

import logic._
import logic.functions._
import controllers.domain.app.protocol.ProfileModificationResult._
import models.domain._

/**
 * All friends related logic.
 */
trait Friends { this: UserLogic =>

  def canShortlist = {
    if (!user.profile.rights.unlockedFunctionality.contains(Functionality.AddToShortList))
      NotEnoughRights
    else if (!(user.profile.assets canAfford costToShortlist))
      NotEnoughAssets
    else
      OK
  }

  def costToShortlist = {
    Assets(coins = costToShortlistPerson(user.profile.publicProfile.level))
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
    val friendAhead = potentialFriend.profile.publicProfile.level - user.profile.publicProfile.level

    if (friendAhead > 0) {
      Assets(money = friendAhead)
    } else {
      Assets(coins = costToInviteFriend(user.profile.publicProfile.level, -friendAhead))
    }
  }

}
