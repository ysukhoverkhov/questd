

package models.store.mongo

import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import play.api.test._
import play.api.test.Helpers._
import models.store._
import models.domain.user._
import play.Logger

//@RunWith(classOf[JUnitRunner])
class ConfiDAOSpecs extends Specification
  with MongoDatabaseComponent
  with BaseDAOSpecs {

  "Mongo Config DAO" should {
    "Return empty configuration" in new WithApplication(appWithTestDatabase) {
      val userid = "lalala"
      db.user.createUser(User(userid))
      val u = db.user.readUserByID(User(userid))
      u must beSome.which((u: User) => u.id.toString == userid)
    }

    "Find user by FB id" in new WithApplication(appWithTestDatabase) {
      val fbid = "idid"
      val testsess = "session name"
      db.user.createUser(User(testsess, fbid))
      val u = db.user.readUserByFBid(fbid)
      u must beSome.which((u: User) => u.id.toString == testsess) and
        beSome.which((u: User) => u.fbid == fbid)
    }.pendingUntilFixed("message about the issue")

    "Find user by session id" in new WithApplication(appWithTestDatabase) {
      val sessid = "idid"
      val testsess = "session name"
      db.user.createUser(User(testsess, None, Some(sessid)))
      val u = db.user.readUserBySessionID(sessid)
      u must beSome.which((u: User) => u.id.toString == testsess) and
        beSome.which((u: User) => u.fbid == None) and
        beSome.which((u: User) => u.session == Some(SessionID(sessid)))
    }.pendingUntilFixed("message about the issue")

    "Update user in DB" in new WithApplication(appWithTestDatabase) {
      val sessid = "old session id"
      val id = "id for test of update"

      db.user.createUser(User(id, None, Some(sessid)))
      val u1 = db.user.readUserBySessionID(sessid)
      val u1unlifted = u1 match {
        case Some(z) => z
        case _ => failure("User not found in database")
      }

      val newsessid = "very new session id"
      db.user.updateUser(u1unlifted.replaceSessionID(newsessid))
      val u2 = db.user.readUserByID(u1unlifted)

      u1 must beSome.which((u: User) => u.id.toString == id) and
        beSome.which((u: User) => u.fbid == None) and
        beSome.which((u: User) => u.session == Some(SessionID(sessid)))
      u2 must beSome.which((u: User) => u.id.toString == id) and
        beSome.which((u: User) => u.fbid == None) and
        beSome.which((u: User) => u.session == Some(SessionID(newsessid)))
    }.pendingUntilFixed("message about the issue")

    "Delete user in DB" in new WithApplication(appWithTestDatabase) {
      val userid = "id to test delete"

      db.user.createUser(User(userid))
      db.user.readUserByID(User(userid))
      db.user.deleteUser(User(userid))
      val u = db.user.readUserByID(User(userid))

      u must beNone
    }.pendingUntilFixed("message about the issue")

    "List all users in DB" in new WithApplication(appWithTestDatabase) {
      val userid = "id to test all"

      db.user.createUser(User(userid))
      val all = db.user.allUsers

      all must contain(User(userid))
    }.pendingUntilFixed("message about the issue")

    "One more check for listing and deleting everything" in new WithApplication(appWithTestDatabase) {
      db.user.allUsers.foreach(db.user.deleteUser(_))

      val all = db.user.allUsers

      all must haveSize(0)
    }.pendingUntilFixed("message about the issue")

    "Delete user what do not exists" in new WithApplication(appWithTestDatabase) {
      db.user.deleteUser(User("Id of user who never existed in the database"))

    }.pendingUntilFixed("message about the issue")

    """Return "None" in search for not existing user""" in new WithApplication(appWithTestDatabase) {
      val u = db.user.readUserBySessionID("Another id of another neveer existign user")
      u must beNone
    }.pendingUntilFixed("message about the issue")

  }

}

/**
 * Spec with another component setup for testing 
 */
class UserDAOFailSpecs extends Specification
  with MongoDatabaseForTestComponent {

  def testMongoDatabase(name: String = "default"): Map[String, String] = {
    val dbname: String = "questdb-test"
    Map(
      ("mongodb." + name + ".db" -> dbname))
  }
  val appWithTestDatabase = FakeApplication(additionalConfiguration = testMongoDatabase())

  /*
   * Initializing components. It's lazy to let app start first and bring up db driver.
   */
  lazy val db = new MongoDatabaseForTest

  "Mongo User DAO" should {
    "Throw StoreException in case of underlaying error" in new WithApplication(appWithTestDatabase) {
      db.user.createUser(User("tutumc")) must throwA[DatabaseException] 
    }
  }
}

