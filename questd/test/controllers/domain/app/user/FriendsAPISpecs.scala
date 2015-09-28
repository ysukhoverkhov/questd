package controllers.domain.app.user

import controllers.domain.app.protocol.ProfileModificationResult
import controllers.domain.{OkApiResult, BaseAPISpecs}
import models.domain.user.friends.{ReferralStatus, FriendshipStatus, Friendship}
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

      there was one (user).addFriendship(
        friend.id,
        Friendship(
          u.id,
          FriendshipStatus.Accepted,
          ReferralStatus.Refers
        ))
      there was one (user).addFriendship(
        u.id,
        Friendship(
          friend.id,
          FriendshipStatus.Accepted,
          ReferralStatus.ReferredBy,
          Some(contentId)
        ))
    }
  }
}
