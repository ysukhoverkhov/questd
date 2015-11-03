package models.store.dao.user

import models.domain.user.User
import models.domain.user.friends.Friendship

/**
 * DAO for friends related things.
 */
trait UserFriendsDAO {

  def askFriendship(id: String, idToAdd: String, myFriendship: Friendship, hisFriendship: Friendship): Option[User]

  def updateFriendship(id: String, friendId: String, status: Option[String], referralStatus: Option[String]): Option[User]

  def updateFriendship(id: String, friendId: String, myStatus: String, friendStatus: String): Option[User]

  def addFriendship(id: String, friendship: Friendship): Option[User]

  def removeFriendship(id: String, friendId: String): Option[User]
}
