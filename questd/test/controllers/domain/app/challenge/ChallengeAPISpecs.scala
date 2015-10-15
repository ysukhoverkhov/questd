package controllers.domain.app.challenge

import controllers.domain._
import testhelpers.domainstubs._

//noinspection ZeroIndexToHead
class ChallengeAPISpecs extends BaseAPISpecs {

  "Challenge API" should {

    "Return challenge by its id to owner" in context {
      val idMy = "challenge id my"
      val idOther = "challenge id other"
      val u = createUserStub()
      val challengeStub = createChallengeStub(myId = u.id)

      challenge.readById(idMy) returns Some(challengeStub)
      challenge.readById(idOther) returns Some(createChallengeStub())
//      challenge.readById(any) returns None

      api.getChallenge(GetChallengeRequest(
        user = u,
        challengeId = idMy)) must beEqualTo(OkApiResult(GetChallengeResult(GetChallengeCode.OK, Some(challengeStub))))

      api.getChallenge(GetChallengeRequest(
        user = u,
        challengeId = idOther)) must beEqualTo(OkApiResult(GetChallengeResult(GetChallengeCode.UserNotParticipant)))

      there were two(challenge).readById(any)
    }

    "Return correct error if challenge is not in db" in context {
      val idMy = "challenge id my"
      val u = createUserStub()

      challenge.readById(any) returns None

      api.getChallenge(GetChallengeRequest(
        user = u,
        challengeId = idMy)) must beEqualTo(OkApiResult(GetChallengeResult(GetChallengeCode.ChallengeNotFound)))

      there was one(challenge).readById(any)
    }
  }
}

