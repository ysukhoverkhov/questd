package models.store.mongo.dao.user
import com.mongodb.casbah.commons.MongoDBObject
import com.novus.salat._
import models.domain.user.User
import models.domain.user.friends.Friendship
import models.store.dao.user.UserFriendsDAO
import models.store.mongo.helpers.BaseMongoDAO
import models.store.mongo.SalatContext._

/**
 * Mongo DAO implementation.
 */
trait MongoUserFriendsDAO extends UserFriendsDAO {
  this: BaseMongoDAO[User] =>

  /**
   *
   */
  def askFriendship(id: String, idToAdd: String, myFriendship: Friendship, hisFriendship: Friendship): Option[User] = {
    findAndModify(
      id,
      MongoDBObject(
        "$push" -> MongoDBObject(
          "friends" -> grater[Friendship].asDBObject(myFriendship))))

    findAndModify(
      idToAdd,
      MongoDBObject(
        "$push" -> MongoDBObject(
          "friends" -> grater[Friendship].asDBObject(hisFriendship))))
  }

  /**
   * @inheritdoc
   */
  def updateFriendship(id: String, friendId: String, status: Option[String], referralStatus: Option[String]): Option[User] = {

    val queryBuilder = MongoDBObject.newBuilder

    status.foreach { status =>
      queryBuilder += "friends.$.status" -> status
    }

    referralStatus.foreach { referralStatus =>
      queryBuilder += "friends.$.referralStatus" -> referralStatus
    }

    findAndModify(
      MongoDBObject(
        "id" -> id,
        "friends.friendId" -> friendId),
      MongoDBObject(
        "$set" -> queryBuilder.result()))
  }

  /**
   * @inheritdoc
   */
  def addFriendship(id: String, friendship: Friendship): Option[User] = {
    findAndModify(
      id,
      MongoDBObject(
        "$push" -> MongoDBObject(
          "friends" -> grater[Friendship].asDBObject(friendship))))
  }

  /**
   *
   */
  def updateFriendship(id: String, friendId: String, myStatus: String, friendStatus: String): Option[User] = {
    findAndModify(
      MongoDBObject(
        "id" -> id,
        "friends.friendId" -> friendId),
      MongoDBObject(
        "$set" -> MongoDBObject(
          "friends.$.status" -> myStatus)))

    findAndModify(
      MongoDBObject(
        "id" -> friendId,
        "friends.friendId" -> id),
      MongoDBObject(
        "$set" -> MongoDBObject(
          "friends.$.status" -> friendStatus)))
  }

  /**
   *
   */
  def removeFriendship(id: String, friendId: String): Option[User] = {
    findAndModify(
      id,
      MongoDBObject(
        "$pull" -> MongoDBObject(
          "friends" -> MongoDBObject("friendId" -> friendId))))

    findAndModify(
      friendId,
      MongoDBObject(
        "$pull" -> MongoDBObject(
          "friends" -> MongoDBObject("friendId" -> id))))
  }
}
