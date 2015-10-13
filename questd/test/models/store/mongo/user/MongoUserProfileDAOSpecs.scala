package models.store.mongo.user

import models.domain.user.User
import models.domain.user.demo.UserDemographics
import models.store.mongo.BaseDAOSpecs
import play.api.test.WithApplication

/**
 * MongoUserProfileDAO specs
 */
trait MongoUserProfileDAOSpecs { this: BaseDAOSpecs =>

  "Mongo User DAO" should {
    "updateCultureId works" in new WithApplication(appWithTestDatabase) {
      db.user.clear()

      val userid = "updateCultureId"

      db.user.delete(userid)
      db.user.create(
        User(
          id = userid))

      private val cultureId: String = "cult"
      val ou = db.user.updateCultureId(userid, cultureId)

      ou must beSome.which((u: User) => u.id.toString == userid)
      ou must beSome.which((u: User) => u.demo.cultureId.contains(cultureId))
    }

    "replaceCultureIds works" in new WithApplication(appWithTestDatabase) {
      db.user.clear()

      val userid1 = "replaceCultureIds1"
      val userid2 = "replaceCultureIds2"
      val userid3 = "replaceCultureIds3"

      val oldC = "oldC"
      val newC = "newC"

      db.user.create(
        User(
          id = userid1,
          demo = UserDemographics(cultureId = Some(oldC))))

      db.user.create(
        User(
          id = userid2,
          demo = UserDemographics(cultureId = Some(oldC))))

      db.user.create(
        User(
          id = userid3,
          demo = UserDemographics(cultureId = Some(oldC + "1"))))

      db.user.replaceCultureIds(oldC, newC)

      val ou1 = db.user.readById(userid1)
      ou1 must beSome.which((u: User) => u.id.toString == userid1)
      ou1 must beSome.which((u: User) => u.demo.cultureId.contains(newC))

      val ou2 = db.user.readById(userid2)
      ou2 must beSome.which((u: User) => u.id.toString == userid2)
      ou2 must beSome.which((u: User) => u.demo.cultureId.contains(newC))

      val ou3 = db.user.readById(userid3)
      ou3 must beSome.which((u: User) => u.id.toString == userid3)
      ou3 must beSome.which((u: User) => u.demo.cultureId.contains(oldC + "1"))
    }
  }
}
