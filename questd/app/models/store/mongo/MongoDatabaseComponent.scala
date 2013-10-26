package models.store.mongo

import models.store._
import models.store.DAOs._
import dao.user._

private[store] trait MongoDatabaseComponent extends DatabaseComponent {

  class MongoDatabase extends Database with MongoUserDAO
  

  
}

