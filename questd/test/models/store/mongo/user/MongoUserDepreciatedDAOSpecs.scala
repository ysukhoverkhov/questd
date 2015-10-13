package models.store.mongo.user

import models.domain.user.User
import models.store.mongo.BaseDAOSpecs
import play.api.test.WithApplication

/**
 * MongoUserDepreciatedDAO specs
 */
trait MongoUserDepreciatedDAOSpecs { this: BaseDAOSpecs =>
  "Mongo User DAO" should {
    "populate mustVoteSolutions list" in new WithApplication(appWithTestDatabase) {
      db.user.clear()

      val userids = List("replaceCultureIds1", "replaceCultureIds2", "replaceCultureIds3")
      val solIds = List("solId0", "solId1")

      userids.foreach(v => db.user.create(User(id = v)))


      db.user.populateMustVoteSolutionsList(userids, solIds.head)
      db.user.populateMustVoteSolutionsList(userids.tail, solIds(1))

      val ou1 = db.user.readById(userids.head)
      ou1 must beSome.which((u: User) => u.mustVoteSolutions == List(solIds.head))

      val ou2 = db.user.readById(userids(1))
      ou2 must beSome.which((u: User) => solIds forall u.mustVoteSolutions.contains)

      val ou3 = db.user.readById(userids(1))
      ou3 must beSome.which((u: User) => solIds forall u.mustVoteSolutions.contains)
    }

    "removeMustVoteSolution removes it actually" in new WithApplication(appWithTestDatabase) {
      db.user.clear()

      val sol = "solid"
      val u = User(id = "idid", mustVoteSolutions = List(sol))

      db.user.create(u)
      db.user.removeMustVoteSolution(u.id, sol)

      val ou1 = db.user.readById(u.id)
      ou1 must beSome//.which((u: User) => u.mustVoteSolutions == List.empty)
    }
  }
}
