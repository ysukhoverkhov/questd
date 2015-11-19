package models.store.mongo.dao.user
import com.mongodb.casbah.commons.MongoDBObject
import models.domain.user.User
import models.store.dao.user.UserBannedDAO
import models.store.mongo.helpers.BaseMongoDAO

/**
 * Mongo DAO implementation.
 */
trait MongoUserBannedDAO extends UserBannedDAO {
  this: BaseMongoDAO[User] =>

  /**
   * @inheritdoc
   */
  def addBannedUser(id: String, bannedUserId: String): Option[User] = {
    findAndModify(
      id,
      MongoDBObject(
        "$addToSet" -> MongoDBObject(
          "banned" -> bannedUserId)))
  }

  /**
   * @inheritdoc
   */
  def removeBannedUser(id: String, bannedUserId: String): Option[User] = {
    findAndModify(
      id,
      MongoDBObject(
        "$pull" -> MongoDBObject(
          "banned" -> bannedUserId)))
  }
}
