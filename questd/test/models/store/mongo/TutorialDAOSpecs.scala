

package models.store.mongo

import models.domain._
import org.specs2.mutable._
import play.api.test.WithApplication

//@RunWith(classOf[JUnitRunner])
class TutorialDAOSpecs extends Specification
  with MongoDatabaseComponent
  with BaseDAOSpecs {

  private[this] def clearDB() = {
    db.tutorial.clear()
  }

  "Mongo Tutorial DAO" should {

    "Create and read tuorials" in new WithApplication(appWithTestDatabase) {

      clearDB()

      val tc = TutorialCondition(TutorialConditionType.ProfileVariableState, params = Map("param" -> "value"))
      val tt = TutorialTrigger(TutorialTriggerType.Any)
      val te = TutorialElement(
        action = TutorialAction(TutorialActionType.PlayAnimation, params = Map("clip" -> "1.mpg")),
        conditions = List(tc, tc),
        triggers = List(tt, tt))

      val t = Tutorial("iphone", List(te, te))


      db.tutorial.create(t)

      val ot = db.tutorial.readById(t.id)

      ot must beSome.which(_.id == t.id)
    }
  }
}

