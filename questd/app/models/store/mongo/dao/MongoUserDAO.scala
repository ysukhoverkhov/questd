package models.store.mongo.dao

import java.util.Date
import scala.language.postfixOps
import scala.language.implicitConversions
import play.Logger
import play.api.db._
import play.api.Play._

import se.radley.plugin.salat._
import com.novus.salat._
import com.novus.salat.annotations._
import com.novus.salat.dao._
import com.mongodb.casbah.Imports._
import com.mongodb.CommandResult
import com.mongodb.DBObject

import models.store.mongo.SalatContext._
import models.store.mongo.helpers._
import models.store.dao._
import models.store._

import models.domain.user._

/**
 * This class is representing user form the database.
 */
private[mongo] case class UserDB(
  userid: Option[String] = None,
  fbid: Option[String] = None,
  session: Option[String] = None)

private[mongo] object user {

  /**
   * Conversion from domain object  to db object
   */
  implicit def domainToDB(dom: User): UserDB = {
    val sess = dom.session match {
      case None => None
      case Some(v) => Some(v.toString)
    }

    UserDB(Some(dom.id.toString()), dom.fbid, sess)
  }

  /**
   * Conversion from db object to domain object
   */
  implicit def dbToDomain(db: UserDB): User = {
    val ses = db.session match {
      case None => None
      case Some(s) => Some(SessionID(s))
    }
    val uid: UserID = db.userid match {
      case None => UserID.default
      case Some(s) => s
    }

    User(uid, db.fbid, ses)
  }

  /**
   * DOA for User objects
   */
  class MongoUserDAO
    extends UserDAO
    with ModelCompanion[UserDB, ObjectId]
    with BaseDAO[UserDB] {

    val dao = new SalatDAO[UserDB, ObjectId](collection = mongoCollection("users")) {}

    /**
     * Searches for object by query object.
     */
    private def find(user: UserDB): Option[User] = {
      findOne(makeQueryObject(user)) match {
        case None => None
        case Some(o) => Some(o)
      }
    }

    /**
     * Create
     */
    def createUser(u: User): Unit = create(u)
    
    /**
     * Read by userid
     */
    def readUserByID(u: User): Option[User] = wrapMongoException {
      find(UserDB(Some(u.id.toString), None, None))
    }

    /**
     * Read
     */
    def readUserBySessionID(sessid: SessionID): Option[User] = wrapMongoException {
      find(UserDB(None, None, Some(sessid.toString)))
    }

    /**
     * Read
     */
    def readUserByFBid(fbid: String): Option[User] = wrapMongoException {
      find(UserDB(None, Some(fbid), None))
    }

    /**
     * Update by userid.
     */
    def updateUser(u: User): Unit = wrapMongoException {

      Logger.debug("Updating user in database " + u.toString)

      val q = makeQueryObject(UserDB(Some(u.id.toString), None, None))
      val dbo = toDBObject(u)
      val wr = dao.update(q, dbo, false, false)

      if (!wr.getLastError().ok) {
        throw new DatabaseException(wr.getLastError().getErrorMessage())
      }

      Logger.debug("User update in db successfuly " + u.toString)
    }

    /**
     * Delete by userid
     */
    def deleteUser(u: User): Unit = wrapMongoException {
      val wr = remove(makeQueryObject(UserDB(Some(u.id.toString), None, None)))

      if (!wr.getLastError().ok)
        throw new DatabaseException(wr.getLastError().getErrorMessage())
    }

    /**
     * All objects
     */
    def allUsers: List[User] = all map { u => dbToDomain(u) }

  }

  /**
   * Test version of dao what fails al the time
   */
  class MongoUserDAOForTest extends MongoUserDAO {
    override val dao = new SalatDAO[UserDB, ObjectId](collection = MongoConnection("localhost", 55555)("test_db")("test_coll")) {}

  }

}

