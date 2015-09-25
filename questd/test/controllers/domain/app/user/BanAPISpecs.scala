package controllers.domain.app.user

import controllers.domain._
import controllers.domain.app.protocol.ProfileModificationResult
import models.domain.user.friends.{Friendship, FriendshipStatus}
import models.domain.user.timeline.TimeLineReason
import org.mockito.Mockito._
import testhelpers.domainstubs._

//noinspection ZeroIndexToHead
class BanAPISpecs extends BaseAPISpecs {

  "Ban API" should {

    "Ban users correctly" in context {
      val bannedUserId = "bannedUserId"
      val timeLineEntry = createTimeLineEntryStub(actorId = bannedUserId)
      val me = createUserStub(
        following = List(bannedUserId),
        friends = List(
          Friendship(friendId = bannedUserId, status = FriendshipStatus.Accepted)
        ),
        timeLine = List(timeLineEntry))

      val banned = createUserStub(id = bannedUserId)

      db.user.readById(banned.id) returns Some(banned)
      db.user.updateTimeLineEntry(me.id, timeLineEntry.id, TimeLineReason.Hidden) returns Some(me)
      doReturn(OkApiResult(RemoveFromFollowingResult(ProfileModificationResult.OK, Some(me.profile)))).when(api).removeFromFollowing(any)
      doReturn(OkApiResult(RemoveFromFriendsResult(ProfileModificationResult.OK))).when(api).removeFromFriends(any)
      db.user.addBannedUser(me.id, banned.id) returns Some(me)

      val result = api.banUser(
        BanUserRequest(
          user = me,
          userId = bannedUserId))

      result must beAnInstanceOf[OkApiResult[BanUserResult]]

      there was one(user).readById(banned.id)
      there was one(user).updateTimeLineEntry(me.id, timeLineEntry.id, TimeLineReason.Hidden)
      there was one(api).removeFromFollowing(RemoveFromFollowingRequest(me, banned.id))
      there was one(api).removeFromFriends(RemoveFromFriendsRequest(me, banned.id))
      there was one(user).addBannedUser(me.id, banned.id)
    }

    "Remove banned users from friends" in context {
      val bannedUserId = "bannedUserId"
      val me = createUserStub(
        friends = List(
          Friendship(friendId = bannedUserId, status = FriendshipStatus.Invited)
        ))
      val banned = createUserStub(id = bannedUserId)
      solution.readById(mySolutionId) returns Some(sol)
      challenge.findByParticipantsAndQuest((u1.id, opponent.id), questId) returns Iterator.empty

      val result = api.makeSolutionChallenge(MakeSolutionChallengeRequest(
        user = u1,
        opponentId = opponent.id,
        mySolutionId = mySolutionId))

      Logger.error(s"$result")

      result must beAnInstanceOf[OkApiResult[MakeSolutionChallengeResult]]

      there was one(challenge).create(any)
      there was one(api).makeTask(any)
    }

    "respondBattleRequest creates battle" in context {

      db.user.readById(banned.id) returns Some(banned)
      doReturn(OkApiResult(RespondFriendshipResult(ProfileModificationResult.OK))).when(api).respondFriendship(any)

      db.user.addBannedUser(me.id, banned.id) returns Some(me)

      val result = api.banUser(
        BanUserRequest(
          user = me,
          userId = bannedUserId))

      result must beAnInstanceOf[OkApiResult[BanUserResult]]

      there was one(user).readById(banned.id)
      there was one(api).removeFromFriends(RemoveFromFriendsRequest(me, banned.id))

      there was one(user).addBannedUser(me.id, banned.id)
    }

    "Remove banned users from friends again" in context {
      val bannedUserId = "bannedUserId"
      val me = createUserStub(
        friends = List(
          Friendship(friendId = bannedUserId, status = FriendshipStatus.Invites)
        ))
      val banned = createUserStub(id = bannedUserId)

      db.user.readById(banned.id) returns Some(banned)
      doReturn(OkApiResult(RespondFriendshipResult(ProfileModificationResult.OK))).when(api).respondFriendship(any)

      db.user.addBannedUser(me.id, banned.id) returns Some(me)

      val result = api.banUser(
        BanUserRequest(
          user = me,
          userId = bannedUserId))

      result must beAnInstanceOf[OkApiResult[BanUserResult]]

      there was one(user).readById(banned.id)
      there was one(api).respondFriendship(any)

      there was one(user).addBannedUser(me.id, banned.id)
    }
  }
}

