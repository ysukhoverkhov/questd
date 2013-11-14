package models.store.mongo.dao

import models.store.mongo.helpers._
import models.store.dao._
import models.store._
import models.domain._
import play.Logger


/**
 * DOA for User objects
 */
private[mongo] class MongoUserDAO
  extends BaseMongoDAO[User](collectionName = "users", keyFieldName = "id.id")
  with UserDAO {


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
  def allUsers: Iterator[User] = all 

}

/**
 * Test version of dao what fails al the time
 */

import com.novus.salat.dao.SalatDAO
import org.bson.types.ObjectId
import models.store.mongo.SalatContext._
import com.mongodb.casbah.MongoConnection

class MongoUserDAOForTest extends MongoUserDAO {
  override val dao = new SalatDAO[User, ObjectId](collection = MongoConnection("localhost", 55555)("test_db")("test_coll")) {}

}


