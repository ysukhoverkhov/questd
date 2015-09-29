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

      result must beEqualTo(OkApiResult(RespondFriendshipResult(ProfileModificationResult.OK)))

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
  }
}
