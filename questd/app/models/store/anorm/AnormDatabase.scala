package models.store.anorm

import models.store._
import models.store.DAOs._
import dao.user._

private[store] class AnormDatabase extends Database {

  def user: UserDAO = AnormUserDAO

  
}

private[store] object AnormDatabase extends AnormDatabase


