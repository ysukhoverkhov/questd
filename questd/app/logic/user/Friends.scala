package logic.user

import controllers.domain.app.protocol.ProfileModificationResult._
import logic._
import logic.functions._
import models.domain.common.Assets
import models.domain.user.User
import models.domain.user.friends.FriendshipStatus
import models.domain.user.profile.Functionality

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

  def canFollowUser(potentialFollowingUserId: String) = {
    canFollow match {
      case OK =>
        if (potentialFollowingUserId == user.id)
          InvalidState
        else
          OK
      case a => a
    }
  }

  def costToFollowing = {
    Assets(coins = costToFollowPerson(user.profile.publicProfile.level))
  }

  private def commonFriendshipCheck(potentialFriend: User) = {
    if (potentialFriend.id == user.id)
      InvalidState
    else
      OK
  }

  // TODO: check here we are not banned and return status if we are.
  def canAddFriend(potentialFriend: User) = {
    commonFriendshipCheck(potentialFriend) match {
      case OK =>
        if (user.friends.length >= user.profile.rights.maxFriendsCount)
          LimitExceeded
        else if (!user.profile.rights.unlockedFunctionality.contains(Functionality.InviteFriends))
          NotEnoughRights
        else if (!(user.profile.assets canAfford costToAddFriend(potentialFriend)))
          NotEnoughAssets
        else
          OK
      case a => a
    }
  }

  def canAcceptFriendship(potentialFriend: User) = {
    commonFriendshipCheck(potentialFriend) match {
      case OK =>
        if (user.friends.count(_.status == FriendshipStatus.Accepted) >= user.profile.rights.maxFriendsCount) {
          LimitExceeded
        } else {
          OK
        }
      case a => a
    }
  }

  def costToAddFriend(potentialFriend: User) = {
    Assets(coins = costToInviteFriend(potentialFriend.profile.publicProfile.level))
  }
}

