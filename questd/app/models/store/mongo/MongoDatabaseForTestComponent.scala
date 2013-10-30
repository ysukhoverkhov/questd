package models.store.mongo

import models.store._
import models.store.dao._
import dao.user._

private[store] trait MongoDatabaseForTestComponent extends DatabaseComponent {

  class MongoDatabaseForTest extends Database with MongoUserDAOForTest
  

  
}

