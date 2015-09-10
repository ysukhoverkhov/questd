package logic.user

import logic.BaseLogicSpecs
import models.domain.common.Assets
import models.domain.user._
import models.domain.user.profile.{Rights, PublicProfile, Profile, Bio}
import testhelpers.domainstubs._

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

    "Calculate correct quest level" in {
      createUserStub(level = 7).calculateQuestLevel must beOneOf(1, 2)
      createUserStub(level = 9).calculateQuestLevel must beOneOf(4, 5)
      createUserStub(level = 13).calculateQuestLevel must beOneOf(9, 10)
      createUserStub(level = 17).calculateQuestLevel must beOneOf(15, 16)
      createUserStub(level = 20).calculateQuestLevel must beEqualTo(20)
    }
  }
}

