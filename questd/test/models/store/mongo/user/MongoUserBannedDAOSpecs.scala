package models.store.mongo.user

import models.store.mongo.BaseDAOSpecs
import play.api.test.WithApplication
import testhelpers.domainstubs._

/**
 * MongoUserBannedDAO specs
 */
trait MongoUserBannedDAOSpecs { this: BaseDAOSpecs =>
  "Mongo User DAO" should {
    "Adds and removes banned user correctly" in new WithApplication(appWithTestDatabase) {
      db.user.clear()

      val bannedId = "bannedId"
      val user = createUserStub()

      db.user.create(user)

      val ou1 = db.user.addBannedUser(user.id, bannedId)
      ou1.get.banned must beEqualTo(List(bannedId))

      val ou2 = db.user.addBannedUser(user.id, bannedId)
      ou2.get.banned must beEqualTo(List(bannedId))

      val ou3 = db.user.removeBannedUser(user.id, bannedId)
      ou3.get.banned must beEqualTo(List.empty)
    }
  }
}
