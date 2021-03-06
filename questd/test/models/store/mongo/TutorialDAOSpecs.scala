

package models.store.mongo

import models.domain.common.ClientPlatform
import models.domain.tutorial._
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

    "Create and read tutorials" in new WithApplication(appWithTestDatabase) {

      clearDB()

      val ta = TutorialAction(TutorialActionType.PlayAnimation, params = Map("clip" -> "1.mpg"))
      val tc = TutorialCondition(TutorialConditionType.ProfileVariableState, params = Map("param" -> "value"))
      val tt = TutorialTrigger(TutorialTriggerType.Any)
      val te = TutorialElement(
        actions = List(ta, ta),
        conditions = List(tc, tc),
        triggers = List(tt, tt))

      val t = Tutorial("iphone", List(te, te))


      db.tutorial.create(t)

      val ot = db.tutorial.readById(t.id)

      ot must beSome.which(_.id == t.id)
    }

    "Add element to tutorial" in new WithApplication(appWithTestDatabase) {
      clearDB()

      val ta = TutorialAction(TutorialActionType.PlayAnimation, params = Map("clip" -> "1.mpg"))
      val tc = TutorialCondition(TutorialConditionType.ProfileVariableState, params = Map("param" -> "value"))
      val tt = TutorialTrigger(TutorialTriggerType.Any)
      val te = TutorialElement(
        actions = List(ta, ta),
        conditions = List(tc, tc),
        triggers = List(tt, tt))

      private val platform = ClientPlatform.iPhone.toString
      val t = Tutorial(platform, List.empty)
      db.tutorial.create(t)

      val ot = db.tutorial.addElement(platform, te)
      ot must beSome.which(_.id == t.id)
      ot must beSome.which(_.elements.length == 1)
    }

    "Remove element to tutorial" in new WithApplication(appWithTestDatabase) {
      clearDB()

      val ta = TutorialAction(TutorialActionType.PlayAnimation, params = Map("clip" -> "1.mpg"))
      val tc = TutorialCondition(TutorialConditionType.ProfileVariableState, params = Map("param" -> "value"))
      val tt = TutorialTrigger(TutorialTriggerType.Any)
      val te = TutorialElement(
        actions = List(ta, ta),
        conditions = List(tc, tc),
        triggers = List(tt, tt))

      private val platform = ClientPlatform.iPhone.toString
      val t = Tutorial(platform, List(te))
      db.tutorial.create(t)

      val ot = db.tutorial.deleteElement(platform, te.id)
      ot must beSome.which(_.id == t.id)
      ot must beSome.which(_.elements.isEmpty)
    }

    "Update element to tutorial" in new WithApplication(appWithTestDatabase) {
      clearDB()

      val ta = TutorialAction(TutorialActionType.PlayAnimation, params = Map("clip" -> "1.mpg"))
      val tc = TutorialCondition(TutorialConditionType.ProfileVariableState, params = Map("param" -> "value"))
      val tt = TutorialTrigger(TutorialTriggerType.Any)
      val te = TutorialElement(
        actions = List(ta, ta),
        conditions = List(tc, tc),
        triggers = List(tt, tt))

      val updatedElement = te.copy(actions = List(te.actions.head.copy(actionType = TutorialActionType.Message)))

      private val platform = ClientPlatform.iPhone.toString
      val t = Tutorial(platform, List(te))
      db.tutorial.create(t)

      val ot = db.tutorial.updateElement(platform, updatedElement)
      ot must beSome.which(_.id == t.id)
      ot must beSome.which(_.elements.length == 1)
      ot must beSome.which(_.elements.head.actions.head.actionType == TutorialActionType.Message)
    }
  }
}

