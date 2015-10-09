package models.store.dao.user

import models.domain.user.User

/**
 * DAO related to fetching users from bd.
 */
trait UserFetchDAO {

  def readBySessionId(sessionid: String): Option[User]

  def readBySNid(snName: String, snid: String): Option[User]
}
