package logic.user

import models.domain._

class UserLogicSpecs extends BaseUserLogicSpecs {

  "User Logic" should {

    // TODO: clean me up.
//    "Allow user without coins purchase themes" in {
//      val u = createUserStub(
//        level = 20,
//        assets = Assets(0, 0, 0),
//        questProposalCooldown = new Date(0),
//        takenTheme = None)
//
//      u.canPurchaseQuestProposals must beEqualTo(OK)
//    }
//
//    "Do not allow user with low level purchase themes" in {
//      val u = User(id = "",
//        profile = Profile(publicProfile = PublicProfile(level = 1), assets = Assets(100000, 100000, 1000000)))
//
//      u.canPurchaseQuestProposals must beEqualTo(NotEnoughRights)
//    }
//
//    "Allow user with level and money purchase themes" in {
//      val u = createUserStub(
//        level = 12,
//        assets = Assets(100000, 100000, 1000000),
//        questProposalCooldown = new Date(0),
//        takenTheme = None)
//
//      u.canPurchaseQuestProposals must beEqualTo(OK)
//    }
//
//    "Report cooldiwn if not enough time passed since last try" in {
//      import com.github.nscala_time.time.Imports._
//
//      val u = User(
//        id = "",
//        profile = Profile(
//          publicProfile = PublicProfile(level = 12),
//          assets = Assets(100000, 100000, 1000000),
//          rights = Rights.full,
//          questProposalContext = QuestProposalConext(questProposalCooldown = (DateTime.now + Hours.hours(1)).toDate)))
//
//      u.canPurchaseQuestProposals must beEqualTo(CoolDown)
//    }

    "Report different cooldown for users in different timezone" in {

      val u1 = User(
        id = "",
        profile = Profile(
          publicProfile = PublicProfile(level = 12, bio = Bio(timezone = 0)),
          assets = Assets(100000, 100000, 1000000),
          rights = Rights.full))

      val t1 = u1.getCoolDownForQuestCreation

      val u2 = User(
        id = "",
        profile = Profile(
          publicProfile = PublicProfile(level = 12, bio = Bio(timezone = 1)),
          assets = Assets(100000, 100000, 1000000),
          rights = Rights.full))

      val t2 = u2.getCoolDownForQuestCreation

      t2.before(t1) must beEqualTo(true)
    }

  }
}


