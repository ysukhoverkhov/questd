package models.store.dao

import models.domain._

trait UserDAO {

  def createUser(u: User): Unit
  def readUserByID(key: UserID): Option[User]
  def readUserBySessionID(sessionid: SessionID): Option[User]
  def readUserByFBid(fbid: String): Option[User]
  def updateUser(u: User): Unit
  def deleteUser(key: UserID): Unit
  def allUsers: List[User]

}

