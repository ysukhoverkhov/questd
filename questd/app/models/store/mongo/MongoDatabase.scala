package models.store.mongo

import models.store._
import models.store.DAOs._
import dao.user._

private[store] class MongoDatabase extends Database {

  def user: UserDAO = MongoUserDAO

  
}

private[store] object MongoDatabase extends MongoDatabase


