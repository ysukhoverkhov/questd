package logic.user

import logic.BaseLogicSpecs
import models.domain._

class UserLogicSpecs extends BaseLogicSpecs {

  "User Logic" should {

    "Report different cool down for users in different timezone" in {

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


