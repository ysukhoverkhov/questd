package models.store

object DAOs {

  abstract class DAO[T] {
    def create(t: T): Unit
    def read(t: T): Option[T]
    def update(t: T): Unit
    def delete(t: T): Unit
    def all: List[T]
  }

  import models.domain.user._
  abstract class UserDAO extends DAO[User] {
    def read(sessionid: SessionID): Option[User]
    def readByFBid(fbid: String): Option[User]
  }

}
