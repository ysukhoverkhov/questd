package models.store.dao.user

import models.domain.user.User

/**
 * DAO for banning users.
 */
trait UserBannedDAO {

  /**
   * Adds user to banned list.
   *
   * @param id Our id.
   * @param bannedUserId Id of banned user.
   * @return Updated user structure.
   */
  def addBannedUser(id: String, bannedUserId: String): Option[User]

  /**
   * Removes user from banned list.
   *
   * @param id Our id.
   * @param bannedUserId Id of banned user.
   * @return Updated user structure.
   */
  def removeBannedUser(id: String, bannedUserId: String): Option[User]
}
