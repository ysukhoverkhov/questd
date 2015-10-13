package logic

import java.util.Date

import models.domain.challenge.ChallengeStatus
import testhelpers.domainstubs._

class ChallengeLogicSpecs extends BaseLogicSpecs {

  "Challenge logic" should {

    "Decide to auto reject correct challenges" in context {
      val c1 = createChallengeStub(status = ChallengeStatus.Requested)
      val c2 = createChallengeStub(status = ChallengeStatus.Requested, creationDate = new Date(0))

      c1.shouldBeAutoRejected must beFalse
      c2.shouldBeAutoRejected must beTrue
    }
  }
}

