package models.store.mongo.user

import models.domain.common.ClientPlatform
import models.domain.user.User
import models.domain.user.profile.TutorialState
import models.store.mongo.BaseDAOSpecs
import play.api.test.WithApplication
import testhelpers.domainstubs._

/**
 * MongoUserTutorialDAO specs
 */
trait MongoUserTutorialDAOSpecs { this: BaseDAOSpecs =>

  "Mongo User DAO" should {
    "addClosedTutorialElement works" in new WithApplication(appWithTestDatabase) {

      val userid = "addTasksTest"
      val elementid = "elementid"

      db.user.delete(userid)
      db.user.create(
        User(
          id = userid))

      val ou = db.user.addClosedTutorialElement(userid, ClientPlatform.iPhone.toString, elementid)

      ou must beSome.which((u: User) => u.id.toString == userid)
      ou must beSome.which(
        (u: User) => u.profile.tutorialStates(ClientPlatform.iPhone.toString).closedElementIds == List(elementid))
    }

    "addTutorialTaskAssigned works" in new WithApplication(appWithTestDatabase) {

      val userid = "addTasksTest"

      db.user.delete(userid)
      db.user.create(
        User(
          id = userid))

      db.user.addTutorialTaskAssigned(userid, ClientPlatform.iPhone.toString, "t1")
      db.user.addTutorialTaskAssigned(userid, ClientPlatform.iPhone.toString, "t2")
      db.user.addTutorialTaskAssigned(userid, ClientPlatform.iPhone.toString, "t3")
      val ou = db.user.addTutorialTaskAssigned(userid, ClientPlatform.iPhone.toString, "t2")

      ou must beSome.which((u: User) => u.id.toString == userid)
      ou must beSome
        .which((u: User) => u.profile.tutorialStates(ClientPlatform.iPhone.toString).usedTutorialTaskIds.length == 3)
    }

    "addTutorialQuestAssigned works" in new WithApplication(appWithTestDatabase) {

      val userid = "addQuestsTest"

      db.user.delete(userid)
      db.user.create(
        User(
          id = userid))

      db.user.addTutorialQuestAssigned(userid, ClientPlatform.iPhone.toString, "t1")
      db.user.addTutorialQuestAssigned(userid, ClientPlatform.iPhone.toString, "t2")
      db.user.addTutorialQuestAssigned(userid, ClientPlatform.iPhone.toString, "t3")
      val ou = db.user.addTutorialQuestAssigned(userid, ClientPlatform.iPhone.toString, "t2")

      ou must beSome.which((u: User) => u.id.toString == userid)
      ou must beSome
        .which((u: User) => u.profile.tutorialStates(ClientPlatform.iPhone.toString).usedTutorialQuestIds.length == 3)
    }

    "setDailyTasksSuppressed works" in new WithApplication(appWithTestDatabase) {
      db.user.clear()

      val user = createUserStub(tutorialState = TutorialState(dailyTasksSuppression = true))
      user.profile.tutorialStates(ClientPlatform.iPhone.toString).dailyTasksSuppression must beEqualTo(true)

      db.user.create(user)
      val ou = db.user.setDailyTasksSuppressed(
        id = user.id,
        platform = ClientPlatform.iPhone.toString,
        suppressed = false)

      ou must beSome
      ou.get.profile.tutorialStates(ClientPlatform.iPhone.toString).dailyTasksSuppression must beEqualTo(false)
    }
  }
}
