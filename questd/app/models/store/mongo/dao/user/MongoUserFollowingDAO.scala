package models.store.mongo.dao.user
import com.mongodb.casbah.commons.MongoDBObject
import models.domain.user.User
import models.store.dao.user.UserFollowingDAO
import models.store.mongo.helpers.BaseMongoDAO

/**
 * Mongo DAO implementation.
 */
trait MongoUserFollowingDAO extends UserFollowingDAO {
  this: BaseMongoDAO[User] =>

  /**
   * @inheritdoc
   */
  def addToFollowing(id: String, idToAdd: String): Option[User] = {
    findAndModify(
      idToAdd,
      MongoDBObject(
        "$addToSet" -> MongoDBObject(
          "followers" -> id)))

    findAndModify(
      id,
      MongoDBObject(
        "$addToSet" -> MongoDBObject(
          "following" -> idToAdd)))
  }

  /**
   * @inheritdoc
   */
  def removeFromFollowing(id: String, idToRemove: String): Option[User] = {
    findAndModify(
      idToRemove,
      MongoDBObject(
        "$pull" -> MongoDBObject(
          "followers" -> id)))

    findAndModify(
      id,
      MongoDBObject(
        "$pull" -> MongoDBObject(
          "following" -> idToRemove)))
  }
}
