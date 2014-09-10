package models.store.mongo

import models.store.mongo.dao._

private[store] trait MongoDatabaseForTestComponent extends MongoDatabaseComponent {

  class MongoDatabaseForTest extends MongoDatabase {
    override val user = new MongoUserDAOForTest
  }



}

