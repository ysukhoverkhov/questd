package models.store.dao

import models.domain._

trait UserDAO extends BaseDAO[User] {

  def readBySessionID(sessionid: String): Option[User]
  def readByFBid(fbid: String): Option[User]

  def addToAssets(id: String, assets: Assets): Option[User]
}

