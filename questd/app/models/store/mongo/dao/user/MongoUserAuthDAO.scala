package models.store.mongo.dao.user
import java.util.Date

import com.mongodb.casbah.commons.MongoDBObject
import com.novus.salat._
import models.domain.user.User
import models.domain.user.auth.CrossPromotedApp
import models.store.dao.user.UserAuthDAO
import models.store.mongo.helpers.BaseMongoDAO
import models.store.mongo.SalatContext._

/**
 * Mongo DAO implementation.
 */
trait MongoUserAuthDAO extends UserAuthDAO {
  this: BaseMongoDAO[User] =>

  /**
   * Update user's session id
   */
  def updateSessionId(id: String, sessionid: String): Option[User] = {
    findAndModify(
      id,
      MongoDBObject(
        "$set" -> MongoDBObject(
          "auth.session" -> sessionid,
          "auth.lastLogin" -> new Date)))
  }

  /**
   * @inheritdoc
   */
  def addCrossPromotions(id: String, snName: String, apps: List[CrossPromotedApp]): Option[User] = {
    findAndModify(
      MongoDBObject(
        "id" -> id,
        "auth.loginMethods.methodName" -> snName),
      MongoDBObject(
        "$addToSet" -> MongoDBObject(
          "auth.loginMethods.$.crossPromotion.apps" -> MongoDBObject(
            "$each" -> apps.map(grater[CrossPromotedApp].asDBObject)))))
  }
}
