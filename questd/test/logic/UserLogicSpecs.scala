package logic

import org.specs2.mutable._
import org.specs2.runner._
import org.specs2.mock.Mockito
import org.junit.runner._
import logic._
import controllers.domain.user.protocol.ProfileModificationResult._
import models.domain._
import org.joda.time.Hours
import play.Logger

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

    "Report cooldiwn if not enough time passed since last try" in {
      import com.github.nscala_time.time.Imports._
      import java.util.Date

      val u = User(
        id = "",
        profile = Profile(
          level = 12,
          assets = Assets(100000, 100000, 1000000),
          questProposalContext = QuestProposalConext(questProposalCooldown = (DateTime.now + Hours.hours(1)).toDate)))

      u.canPurchaseQuestProposals must beEqualTo(CoolDown)
    }

    "Report different cooldowd for users in different timezone" in {

      import com.github.nscala_time.time.Imports._
      import org.joda.time.DateTime

      val u1 = User(
        id = "",
        profile = Profile(
          level = 12,
          assets = Assets(100000, 100000, 1000000),
          bio = Bio(timezone = 0)))

      val t1 = u1.getCooldownForTakeTheme

      val u2 = User(
        id = "",
        profile = Profile(
          level = 12,
          assets = Assets(100000, 100000, 1000000),
          bio = Bio(timezone = 1)))

      val t2 = u2.getCooldownForTakeTheme
      
      t2.before(t1) must beEqualTo(true)
    }

  }

}


