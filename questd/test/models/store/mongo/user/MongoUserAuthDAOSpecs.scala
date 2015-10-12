package models.store.mongo.user

import models.domain.user.User
import models.domain.user.auth.CrossPromotedApp
import models.store.mongo.BaseDAOSpecs
import play.api.test.WithApplication
import testhelpers.domainstubs._

/**
 * MongoUserAuthDAO specs
 */
trait MongoUserAuthDAOSpecs { this: BaseDAOSpecs =>

  "Mongo User DAO" should {
    "Add cross promotions" in new WithApplication(appWithTestDatabase) {
      val user: User = createUserStub()
      db.user.create(user)

      val u = db.user.addCrossPromotions(
        user.id, "FB", List(CrossPromotedApp("appName", "userId"), CrossPromotedApp("appName2", "userId2")))

      u must beSome
      val u2 = u.get
      u2.auth.loginMethods.find(_.methodName == "FB").get.crossPromotion.apps.length must beEqualTo(2)
      u2.auth.loginMethods.find(_.methodName == "FB").get.crossPromotion.apps.find(_.appName == "appName") must beSome
    }
  }
}
