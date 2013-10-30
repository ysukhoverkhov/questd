package models.store.dao

import models.domain.user._

private[store] trait UserDAO {

  def createUser(u: User): Unit
  def readUserByID(u: User): Option[User]
  def readUserBySessionID(sessionid: SessionID): Option[User]
  def readUserByFBid(fbid: String): Option[User]
  def updateUser(u: User): Unit
  def deleteUser(u: User): Unit
  def allUsers: List[User]

}

