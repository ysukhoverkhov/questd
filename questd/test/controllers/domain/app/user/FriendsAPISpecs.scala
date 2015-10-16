package controllers.domain.app.user

import java.util.Date

import controllers.domain.app.protocol.ProfileModificationResult
import controllers.domain.{BaseAPISpecs, OkApiResult}
import models.domain.user.friends.{Friendship, FriendshipStatus, ReferralStatus}
import org.mockito.Matchers.{eq => mEq}
import org.specs2.matcher.Matcher
import testhelpers.domainstubs._

class FriendsAPISpecs extends BaseAPISpecs {

  "Friends API" should {

    "Stop following users if the become friends" in context {

      val requesterId = "requester_id"
      val responder = createUserStub(friends = List(Friendship(requesterId, FriendshipStatus.Invites)))
      val requester = createUserStub(id = requesterId)

      user.readById(requesterId) returns Some(requester)
      user.addMessage(any, any) returns Some(responder)

      val result = api.respondFriendship(RespondFriendshipRequest(responder, requester.id, accept = true))

      result must beEqualTo(OkApiResult(RespondFriendshipResult(RespondFriendshipCode.OK)))

      there was one (user).removeFromFollowing(responder.id, requester.id)
      there was one (user).removeFromFollowing(requester.id, responder.id)
    }

    "createFriendship creates friendship with correct references" in context {
      val u = createUserStub()
      val friend = createUserStub()
      val contentId = "contentId"

      user.readById(friend.id) returns Some(friend)

      val result = api.createFriendship(
        CreateFriendshipRequest(
          u,
          friend.id,
          isReferredBy = true,
          referredWithContentId = Some(contentId)))

      result must beAnInstanceOf[OkApiResult[CreateFriendshipResult]]


      def beEqualIgnoringDate(a: Friendship): Matcher[Friendship] = {
        beEqualTo(a.copy(creationDate = new Date(0))) ^^ ((_:Friendship).copy(creationDate = new Date(0)))
      }

      there was one (user).addFriendship(
        mEq(friend.id),
        beEqualIgnoringDate(Friendship(
          friendId = u.id,
          status = FriendshipStatus.Accepted,
          referralStatus = ReferralStatus.Refers,
          referredWithContentId = None)
        ))
      there was one (user).addFriendship(
        mEq(u.id),
        beEqualIgnoringDate(Friendship(
          friendId = friend.id,
          status = FriendshipStatus.Accepted,
          referralStatus = ReferralStatus.ReferredBy,
          referredWithContentId = Some(contentId))
        ))
    }

    "Asking friendship generates reject if we are banned" in context {

      val we = createUserStub()
      val friend = createUserStub(banned = List(we.id), friends = List(Friendship(we.id, FriendshipStatus.Invites)))

      db.user.readById(friend.id) returns Some(friend)
      db.user.readById(we.id) returns Some(we)
      doReturn(OkApiResult(AdjustAssetsResult(we))).when(api).adjustAssets(any)
      doReturn(OkApiResult(SendMessageResult(we))).when(api).sendMessage(any)

      val result = api.askFriendship(AskFriendshipRequest(we, friend.id))

      result must beAnInstanceOf[OkApiResult[AskFriendshipResult]]

      there was one (user).removeFriendship(any, any)
      there was one (api).sendMessage(any)
    }

  }
}
