package logic

import org.specs2.mutable._
import org.specs2.runner._
import org.specs2.mock.Mockito
import org.junit.runner._

import logic._
import controllers.domain.user.protocol.ProfileModificationResult._
import models.domain._

class UserLogicSpecs extends Specification
  with Mockito {

  "User Logic" should {

    "Do not allow user without coins purchase themes" in {
      val u = User(id = "",
        profile = Profile(level = 20, assets = Assets(0, 0, 0)))

      u.canPurchaseQuestProposals must beEqualTo(NotEnoughAssets)
    }

    "Do not allow user with low level purchase themes" in {
      val u = User(id = "",
        profile = Profile(level = 1, assets = Assets(100000, 100000, 1000000)))

      u.canPurchaseQuestProposals must beEqualTo(LevelTooLow)
    }

    "Allow user with level and money purchase themes" in {
      val u = User(id = "",
        profile = Profile(level = 12, assets = Assets(100000, 100000, 1000000)))

      u.canPurchaseQuestProposals must beEqualTo(OK)
    }

  }

}


