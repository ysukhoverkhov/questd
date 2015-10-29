

package models.store.mongo

import models.domain.user._
import models.domain.user.auth.AuthInfo
import models.store._
import models.store.mongo.user._
import org.specs2.mutable._
import play.api.test.{WithApplication, _}

// split it on several tests.
class UserDAOSpecs
  extends BaseDAOSpecs
  with MongoUserAuthDAOSpecs
  with MongoUserBannedDAOSpecs
  with MongoUserContextsDAOSpecs
  with MongoUserDailyResultsDAOSpecs
  with MongoUserFetchDAOSpecs
  with MongoUserFriendsDAOSpecs
  with MongoUserMessagesDAOSpecs
  with MongoUserProfileDAOSpecs
  with MongoUserStatsDAOSpecs
  with MongoUserTasksDAOSpecs
  with MongoUserTimeLineDAOSpecs
  with MongoUserTutorialDAOSpecs
{

  "Mongo User DAO" should {
    "Create new User in DB and find it by userId" in new WithApplication(appWithTestDatabase) {
      db.user.clear()
      val userid = "lalala"
      db.user.create(User(userid))
      val u = db.user.readById(userid)
      u must beSome.which((u: User) => u.id.toString == userid)
    }

    "Update user in DB" in new WithApplication(appWithTestDatabase) {
      val sessid = "old session id"
      val id = "id for test of update"

      db.user.create(User(id, AuthInfo(session = Some(sessid))))
      val u1: Option[User] = db.user.readBySessionId(sessid)

      u1 must beSome

      val u1unlifted: User = u1.get

      val newsessid = "very new session id"
      db.user.update(u1unlifted.copy(auth = u1unlifted.auth.copy(session = Some(newsessid))))
      val u2 = db.user.readById(u1unlifted.id)

      u1 must beSome.which((u: User) => u.id.toString == id) and
        beSome.which((u: User) => u.auth.loginMethods == List.empty) and
        beSome.which((u: User) => u.auth.session.contains(sessid))
      u2 must beSome.which((u: User) => u.id.toString == id) and
        beSome.which((u: User) => u.auth.loginMethods == List.empty) and
        beSome.which((u: User) => u.auth.session.contains(newsessid))
    }

    "Delete user in DB" in new WithApplication(appWithTestDatabase) {
      db.user.clear()

      val userid = "id to test delete"

      db.user.create(User(userid))
      db.user.readById(userid)
      db.user.delete(userid)
      val u = db.user.readById(userid)

      u must beNone
    }

    "List all users in DB" in new WithApplication(appWithTestDatabase) {
      val userid = "id to test all"

      db.user.create(User(userid))
      val all = List.empty ++ db.user.all

      all.map(_.id) must contain(userid)
    }

    "One more check for listing and deleting everything" in new WithApplication(appWithTestDatabase) {
      db.user.all.foreach((u: User) => db.user.delete(u.id))

      val all = List.empty ++ db.user.all

      all must haveSize(0)
    }

    "Delete user what do not exists" in new WithApplication(appWithTestDatabase) {
      db.user.delete("Id of user who never existed in the database")
    }

    """Return "None" in search for not existing user""" in new WithApplication(appWithTestDatabase) {
      val u = db.user.readBySessionId("Another id of another never existing user")
      u must beNone
    }

    // TAGS: clean me up.
//    "takeQuest must remember quest's theme in history" in new WithApplication(appWithTestDatabase) {
//      val userId = "takeQuest2"
//      val themeId = "tid"
//
//      db.user.create(User(userId))
//
//      db.user.takeQuest(
//        userId,
//        QuestInfoWithID(
//          "q",
//          QuestInfo(
//            authorId = "authorId",
//            themeId = themeId,
//            content = QuestInfoContent(
//              media = ContentReference(
//                contentType = ContentType.Photo,
//                storage = "",
//                reference = ""),
//              icon = None,
//              description = ""),
//            vip = false)),
//        new Date(),
//        new Date())
//
//      val ou = db.user.readById(userId)
//      ou must beSome.which((u: User) => u.id.toString == userId)
//      ou must beSome.which((u: User) => u.history.themesOfSelectedQuests.contains(themeId))
//    }

    // TAGS: clean me up.
//    "resetTodayReviewedThemes do its work" in new WithApplication(appWithTestDatabase) {
//      val userId = "resetTodayReviewedThemes"
//      val date = new Date(1000)
//
//      db.user.delete(userId)
//      db.user.create(User(
//        id = userId,
//        profile = Profile(
//          questProposalContext = QuestProposalConext(
//            todayReviewedThemeIds = List("lala")))))
//
//      val ou = db.user.resetTodayReviewedThemes(userId)
//
//      ou must beSome.which((u: User) => u.id.toString == userId)
//      ou must beSome.which((u: User) => u.profile.questProposalContext.todayReviewedThemeIds == List.empty)
//    }
  }
}

/**
 * Spec with another component setup for testing
 */
class UserDAOFailSpecs extends Specification
  with MongoDatabaseForTestComponent {

  /*
   * Initializing components. It's lazy to let app start first and bring up db driver.
   */
  lazy val db = new MongoDatabaseForTest
  val appWithTestDatabase = FakeApplication(additionalConfiguration = testMongoDatabase())

  def testMongoDatabase(name: String = "default"): Map[String, String] = {
    val dbname: String = "questdb-test"
    Map(
      "mongodb." + name + ".db" -> dbname)
  }

  "Mongo User DAO" should {
    "Throw StoreException in case of underlaying error" in new WithApplication(appWithTestDatabase) {
      db.user.create(User("tutumc")) must throwA[DatabaseException]
    }
  }
}

