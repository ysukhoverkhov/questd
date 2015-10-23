package logic.user

import controllers.domain.app.user.{AddToFollowingCode, AskFriendshipCode, RespondFriendshipCode}
import logic._
import logic.functions._
import models.domain.common.Assets
import models.domain.user.User
import models.domain.user.friends.{Friendship, FriendshipStatus}
import models.domain.user.profile.Functionality

/**
 * All friends related logic.
 */
trait Friends { this: UserLogic =>

  def canFollow: AddToFollowingCode.Value = {
    import AddToFollowingCode._

    if (!user.profile.rights.unlockedFunctionality.contains(Functionality.AddToFollowing))
      NotEnoughRights
    else if (!(user.profile.assets canAfford costToFollowing))
      NotEnoughAssets
    else
      OK
  }

  def canFollowUser(potentialFollowingUserId: String): AddToFollowingCode.Value = {
    import AddToFollowingCode._

    canFollow match {
      case OK =>
        if (potentialFollowingUserId == user.id)
          CantFollowMySelf
        else
          OK
      case a => a
    }
  }

  def costToFollowing = {
    Assets(coins = costToFollowPerson(user.profile.publicProfile.level))
  }

  def canAddFriend(potentialFriend: User): AskFriendshipCode.Value = {
    import AskFriendshipCode._

    if (user.friends.length >= user.profile.rights.maxFriendsCount)
      MaxFriendsCountLimitReached
    else if (!user.profile.rights.unlockedFunctionality.contains(Functionality.InviteFriends))
      NotEnoughRights
    else if (!(user.profile.assets canAfford costToAddFriend(potentialFriend)))
      NotEnoughAssets
    else if (potentialFriend.id == user.id)
      CantFriendHimself
    else if (potentialFriend.banned.contains(user.id))
      UserBannedByPotentialFriend
    else
      OK
  }

  def canAcceptFriendship(potentialFriend: User): RespondFriendshipCode.Value = {
    import RespondFriendshipCode._

    if (user.friends.count(_.status == FriendshipStatus.Accepted) >= user.profile.rights.maxFriendsCount)
      MaxFriendsCountLimitReached
    else if (potentialFriend.id == user.id)
      CantFriendMyself
    else
      OK
  }

  def costToAddFriend(potentialFriend: User) = {
    Assets(coins = costToInviteFriend(potentialFriend.profile.publicProfile.level))
  }

  def shouldAutoRejectFriendship(friendship: Friendship) = {
    import com.github.nscala_time.time.Imports._
    import org.joda.time.DateTime

    val daysToWait = api.config(api.DefaultConfigParams.RequestsAutoRejectDays).toInt

    (friendship.status == FriendshipStatus.Invites) &&
      (new DateTime(friendship.creationDate) + daysToWait.days < DateTime.now)
  }
}

