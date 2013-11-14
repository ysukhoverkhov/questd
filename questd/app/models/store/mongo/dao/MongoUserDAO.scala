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
  extends BaseMongoDAO[User](collectionName = "users")
  with UserDAO {

  /**
   * Read by session id
   */
  def readBySessionID(sessid: String): Option[User] = {
    readByExample("auth.session", sessid)
  }

  /**
   * Read by fb id
   */
  def readByFBid(fbid: String): Option[User] = {
    readByExample("auth.fbid", fbid)
  }

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


