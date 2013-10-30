package models.store.mongo

import models.store._
import models.store.dao._
import dao.user._
import dao.theme._

trait MongoDatabaseComponent extends DatabaseComponent {

  class MongoDatabase extends Database {

    val user = new MongoUserDAO
    val theme = new MongoThemeDAO
  }

}

