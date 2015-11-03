package models.store.mongo.dao.user
import com.mongodb.casbah.commons.MongoDBObject
import models.domain.user.User
import models.store.dao.user.UserFetchDAO
import models.store.mongo.helpers.BaseMongoDAO

/**
 * Mongo DAO implementation.
 */
trait MongoUserFetchDAO extends UserFetchDAO {
  this: BaseMongoDAO[User] =>

  /**
   * Read by session id
   */
  def readBySessionId(sessionId: String): Option[User] = {
    readByExample("auth.session", sessionId)
  }

  /**
   * Read by fb id
   */
  def readBySNid(snName: String, userId: String): Option[User] = {
    readByExample(
      MongoDBObject(
        "auth.loginMethods.methodName" -> snName,
        "auth.loginMethods.userId" -> userId
      ))
  }
}
