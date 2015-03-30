package controllers.domain.app.user

import controllers.domain.app.protocol.ProfileModificationResult
import controllers.domain.{OkApiResult, BaseAPISpecs}
import models.domain._
import testhelpers.domainstubs._

class FriendsAPISpecs extends BaseAPISpecs {

  "Friends API" should {

    "Stop following users if the become friends" in context {

      val requesterId = "requester_id"
      val responder = createUserStub(friends = List(Friendship(requesterId, FriendshipStatus.Invites)))
      val requester = createUserStub(id = requesterId)

      user.readById(requesterId) returns Some(requester)

      val result = api.respondFriendship(RespondFriendshipRequest(responder, requester.id, accept = true))

      result must beEqualTo(OkApiResult(RespondFriendshipResult(ProfileModificationResult.OK)))

      there was one (user).removeFromFollowing(responder.id, requester.id)
      there was one (user).removeFromFollowing(requester.id, responder.id)
    }

  }
}

