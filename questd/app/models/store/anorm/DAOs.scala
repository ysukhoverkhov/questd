package models.store.anorm

import models.store.DAOs._

private[store] object DAOs {

  import models.domain.user._
  object AnormUserDAO extends UserDAO {

    def create(t: User): Unit = {}
    def read(t: User): User = { null }
    def update(t: User): Unit = {}
    def delete(t: User): Unit = {}
    def all: List[User] = List()

  }

}

