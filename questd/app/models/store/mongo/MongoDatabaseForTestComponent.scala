package models.store.mongo

import models.store._
import models.store.dao._
import dao.user._
import models.store.mongo._

private[store] trait MongoDatabaseForTestComponent extends MongoDatabaseComponent {

  class MongoDatabaseForTest extends MongoDatabase {
    override val user = new MongoUserDAOForTest
  }
  

  
}

