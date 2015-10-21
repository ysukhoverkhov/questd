package models.store.dao.user

import models.domain.user.User

/**
 * DAO for following users
 */
trait UserFollowingDAO {

  def addToFollowing(id: String, idToAdd: String): Option[User]

  def removeFromFollowing(id: String, idToRemove: String): Option[User]
}
