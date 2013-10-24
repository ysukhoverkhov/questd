

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
class UserDAOSpecs extends Specification {

  def inMemoryMongoDatabase(name: String = "default"): Map[String, String] = {
    val dbname: String = "questdb-test"
    Map(
      ("mongodb." + name + ".db" -> dbname))
  }
  val appWithTestDatabase = FakeApplication(additionalConfiguration = inMemoryMongoDatabase())

  
  "Mongo User DAO" should {
    "Create new User in DB and find it by userid" in new WithApplication(appWithTestDatabase) {
      val userid = "lalala"
      Store.user.create(User(userid))
      val u = Store.user.read(User(userid))
      u must beSome.which((u: User) => u.id.toString == userid)
    }

    "Find user by FB id" in new WithApplication(appWithTestDatabase) {
      val fbid = "idid"
      val testsess = "session name"
      Store.user.create(User(testsess, fbid))
      val u = Store.user.readByFBid(fbid)
      u must beSome.which((u: User) => u.id.toString == testsess) and
        beSome.which((u: User) => u.fbid == fbid)
    }

    "Find user by session id" in new WithApplication(appWithTestDatabase) {
      val sessid = "idid"
      val testsess = "session name"
      Store.user.create(User(testsess, None, Some(sessid)))
      val u = Store.user.readBySessionID(sessid)
      u must beSome.which((u: User) => u.id.toString == testsess) and
        beSome.which((u: User) => u.fbid == None) and
        beSome.which((u: User) => u.session == Some(SessionID(sessid)))
    }

    "Update user in DB" in new WithApplication(appWithTestDatabase) {
      val sessid = "old session id"
      val id = "id for test of update"

      Store.user.create(User(id, None, Some(sessid)))
      val u1 = Store.user.readBySessionID(sessid)
      val u1unlifted = u1 match {
        case Some(z) => z
        case _ => failure("User not found in database")
      }

      val newsessid = "very new session id"
      Store.user.update(u1unlifted.replaceSessionID(newsessid))
      val u2 = Store.user.read(u1unlifted)

      u1 must beSome.which((u: User) => u.id.toString == id) and
        beSome.which((u: User) => u.fbid == None) and
        beSome.which((u: User) => u.session == Some(SessionID(sessid)))
      u2 must beSome.which((u: User) => u.id.toString == id) and
        beSome.which((u: User) => u.fbid == None) and
        beSome.which((u: User) => u.session == Some(SessionID(newsessid)))
    }

    "Delete user in DB" in new WithApplication(appWithTestDatabase) {
      val userid = "id to test delete"

      Store.user.create(User(userid))
      Store.user.read(User(userid))
      Store.user.delete(User(userid))
      val u = Store.user.read(User(userid))

      u must beNone
    }

    "List all users in DB" in new WithApplication(appWithTestDatabase) {
      val userid = "id to test all"

      Store.user.create(User(userid))
      val all = Store.user.all

      all must contain(User(userid))
    }

    "One more check for listing and deleting everything" in new WithApplication(appWithTestDatabase) {
      Store.user.all.foreach(Store.user.delete(_))

      val all = Store.user.all

      all must haveSize(0)
    }

  }

}

