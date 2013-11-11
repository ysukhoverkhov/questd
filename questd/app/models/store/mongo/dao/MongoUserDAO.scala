package models.store.mongo.dao

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

import models.domain._

/**
 * DOA for User objects
 */
private[mongo] class MongoUserDAO
  extends UserDAO
  with ModelCompanion[User, ObjectId]
  with BaseMongoDAO[User] {

  val dao = new SalatDAO[User, ObjectId](collection = mongoCollection("users")) {}
  protected final val keyFieldName = "id.id"


  /**
   * Create
   */
  def createUser(u: User): Unit = create(u)

  /**
   * Read by userid
   */
  def readUserByID(key: UserID): Option[User] = read(key)

  /**
   * Read by session id
   */
  def readUserBySessionID(sessid: SessionID): Option[User] = {
    readByExample("auth.session.id", sessid)
  }

  /**
   * Read by fb id
   */
  def readUserByFBid(fbid: String): Option[User] = {
    readByExample("auth.fbid", fbid)
  }

  /**
   * Update by userid.
   */
  def updateUser(u: User): Unit = {
    update(u.id, u)

    Logger.debug("User update in db successfuly " + u.toString)
  }

  /**
   * Delete by userid
   */
  def deleteUser(key: UserID): Unit = delete(key)

  /**
   * All objects
   */
  def allUsers: List[User] = all 

}

/**
 * Test version of dao what fails al the time
 */
class MongoUserDAOForTest extends MongoUserDAO {
  override val dao = new SalatDAO[User, ObjectId](collection = MongoConnection("localhost", 55555)("test_db")("test_coll")) {}

}


