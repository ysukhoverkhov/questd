

package models.store.mongo

import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import play.api.test._
import play.api.test.Helpers._
import models.store._
import models.domain._
import play.Logger
import models.domain.base.ThemeWithID
import models.domain.base.ThemeWithID
import models.domain.base.ThemeWithID
import org.specs2.matcher.BeEqualTo
import com.mongodb.BasicDBList

//@RunWith(classOf[JUnitRunner])
class UserDAOSpecs
  extends Specification
  with MongoDatabaseComponent
  with BaseDAOSpecs {

  "Mongo User DAO" should {
    "Create new User in DB and find it by userid" in new WithApplication(appWithTestDatabase) {
      val userid = "lalala"
      db.user.create(User(userid))
      val u = db.user.readByID(userid)
      u must beSome.which((u: User) => u.id.toString == userid)
    }

    "Find user by FB id" in new WithApplication(appWithTestDatabase) {
      val fbid = "idid"
      val testsess = "session name"
      db.user.create(User(testsess, AuthInfo(fbid = Some(fbid))))
      val u = db.user.readByFBid(fbid)
      u must beSome.which((u: User) => u.id.toString == testsess) and
        beSome.which((u: User) => u.auth.fbid == fbid)
    }

    "Find user by session id" in new WithApplication(appWithTestDatabase) {
      val sessid = "idid"
      val testsess = "session name"
      db.user.create(User(testsess, AuthInfo(session = Some(sessid))))
      val u = db.user.readBySessionID(sessid)
      u must beSome.which((u: User) => u.id.toString == testsess) and
        beSome.which((u: User) => u.auth.fbid == None) and
        beSome.which((u: User) => u.auth.session == Some(sessid))
    }

    "Update user in DB" in new WithApplication(appWithTestDatabase) {
      val sessid = "old session id"
      val id = "id for test of update"

      db.user.create(User(id, AuthInfo(session = Some(sessid))))
      val u1 = db.user.readBySessionID(sessid)
      val u1unlifted = u1 match {
        case Some(z) => z
        case _ => failure("User not found in database")
      }

      val newsessid = "very new session id"
      db.user.update(u1unlifted.copy(auth = u1unlifted.auth.copy(session = Some(newsessid))))
      val u2 = db.user.readByID(u1unlifted.id)

      u1 must beSome.which((u: User) => u.id.toString == id) and
        beSome.which((u: User) => u.auth.fbid == None) and
        beSome.which((u: User) => u.auth.session == Some(sessid))
      u2 must beSome.which((u: User) => u.id.toString == id) and
        beSome.which((u: User) => u.auth.fbid == None) and
        beSome.which((u: User) => u.auth.session == Some(newsessid))
    }

    "Delete user in DB" in new WithApplication(appWithTestDatabase) {
      val userid = "id to test delete"

      db.user.create(User(userid))
      db.user.readByID(userid)
      db.user.delete(userid)
      val u = db.user.readByID(userid)

      u must beNone
    }

    "List all users in DB" in new WithApplication(appWithTestDatabase) {
      val userid = "id to test all"

      db.user.create(User(userid))
      val all = List() ++ db.user.all

      all.map(_.id) must contain(userid)
    }

    "One more check for listing and deleting everything" in new WithApplication(appWithTestDatabase) {
      db.user.all.foreach((u: User) => db.user.delete(u.id))

      val all = List() ++ db.user.all

      all must haveSize(0)
    }

    "Delete user what do not exists" in new WithApplication(appWithTestDatabase) {
      db.user.delete("Id of user who never existed in the database")

    }

    """Return "None" in search for not existing user""" in new WithApplication(appWithTestDatabase) {
      val u = db.user.readBySessionID("Another id of another never existign user")
      u must beNone
    }

    "Purchase quest theme with sample quest" in new WithApplication(appWithTestDatabase) {
      val userid = "lalala2"
      val themeid = "themeid"
      val questdescr = "questdescr"
      val rew = Assets(1, 2, 3)

      db.user.create(User(userid))
      db.user.purchaseQuestTheme(
        userid,
        ThemeWithID(themeid, Theme(text = "text", comment = "comment")),
        Some(QuestInfo(QuestInfoContent(ContentReference("type", "storage", "reference"), None, questdescr))),
        rew)

      val ou = db.user.readByID(userid)

      ou must beSome.which((u: User) => u.id.toString == userid)

      ou.get.profile.questProposalContext.purchasedTheme must beSome.which((t: ThemeWithID) => t.obj.id == themeid) and
        beSome.which((t: ThemeWithID) => t.id == themeid)

      ou.get.profile.questProposalContext.sampleQuest must beSome.which((q: QuestInfo) => q.content.description == questdescr)

      ou.get.profile.questProposalContext.approveReward must beEqualTo(rew)

      ou.get.profile.questProposalContext.numberOfPurchasedThemes must beEqualTo(1)

      ou.get.profile.questProposalContext.todayReviewedThemeIds must contain(themeid)
    }

    "Purchase quest theme without sample quest" in new WithApplication(appWithTestDatabase) {
      val userid = "lalala3"
      val themeid = "themeid"
      val questdescr = "questdescr"
      val rew = Assets(1, 2, 3)

      db.user.create(User(userid))
      db.user.purchaseQuestTheme(
        userid,
        ThemeWithID(themeid, Theme(text = "text", comment = "comment")),
        Some(QuestInfo(QuestInfoContent(ContentReference("type", "storage", "reference"), None, questdescr))),
        rew)
      db.user.purchaseQuestTheme(
        userid,
        ThemeWithID(themeid, Theme(text = "text", comment = "comment")),
        None,
        rew)

      val ou = db.user.readByID(userid)

      ou must beSome.which((u: User) => u.id.toString == userid)

      ou.get.profile.questProposalContext.purchasedTheme must beSome.which((t: ThemeWithID) => t.obj.id == themeid) and
        beSome.which((t: ThemeWithID) => t.id == themeid)

      ou.get.profile.questProposalContext.sampleQuest must beNone

      ou.get.profile.questProposalContext.approveReward must beEqualTo(rew)

      ou.get.profile.questProposalContext.numberOfPurchasedThemes must beEqualTo(2)

      ou.get.profile.questProposalContext.todayReviewedThemeIds must contain(themeid)
    }

    "rememberProposalVotingInHistory should remember liked proposals" in new WithApplication(appWithTestDatabase) {
      val userid = "rememberProposalVotingInHistory"
      val q1id = "q1id"
      val q2id = "q2id"
        
      db.user.create(User(userid))

      db.user.rememberProposalVotingInHistory(userid, q1id, true)
      db.user.rememberProposalVotingInHistory(userid, q2id, false)

      val ou = db.user.readByID(userid)
      ou must beSome.which((u: User) => u.id.toString == userid)

      val arr1 = ou.get.history.likedQuestProposalIds.asInstanceOf[List[BasicDBList]](0).toArray().collect{ case s: String => s }
      arr1.size must beEqualTo(3) // 2 is "", "" stub in list of lists.
      arr1(2) must beEqualTo(q1id)

      val arr2 = ou.get.history.votedQuestProposalIds.asInstanceOf[List[BasicDBList]](0).toArray().collect{ case s: String => s }
      arr2.size must beEqualTo(4) // 2 is "", "" stub in list of lists.
      arr2(2).asInstanceOf[String] must beEqualTo(q1id)
      arr2(3).asInstanceOf[String] must beEqualTo(q2id)
    }
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
      db.user.create(User("tutumc")) must throwA[DatabaseException]
    }
  }
}

