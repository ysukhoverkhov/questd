

package models.store.mongo

import models.domain._
import org.specs2.mutable._
import play.api.test.WithApplication
import testhelpers.domainstubs._

//@RunWith(classOf[JUnitRunner])
class BattleDAOSpecs extends Specification
  with MongoDatabaseComponent
  with BaseDAOSpecs {

  private[this] def clearDB() = {
    db.battle.clear()
  }

  "Mongo Battle DAO" should {
    "Update battle" in new WithApplication(appWithTestDatabase) {
      // TODO: implement me.
      clearDB()

//      val id = "ididiid"
//
//      createSolutionInDB(id)
//
//      val q = db.solution.readById(id)
//
//      q must beSome[Solution].which(_.id == id)
      success
    }

  }
}

