

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
      clearDB()

      val battle = createBattleStub(status = BattleStatus.Fighting)

      db.battle.create(battle)
      db.battle.updateStatus(battle.id, BattleStatus.Resolved)

      var r = db.battle.readById(battle.id)

      r must beSome.which(_.status == BattleStatus.Resolved)
    }
  }
}

