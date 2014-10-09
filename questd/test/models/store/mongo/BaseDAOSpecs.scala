package models.store.mongo

import play.api.test.FakeApplication

trait BaseDAOSpecs { this: MongoDatabaseComponent =>

  def testMongoDatabase(name: String = "default"): Map[String, String] = {
    val dbname: String = "questdb-test-0-30"
    Map(
      "mongodb." + name + ".db" -> dbname)
  }
  val appWithTestDatabase = FakeApplication(additionalConfiguration = testMongoDatabase())

  /*
   * Initializing components. It's lazy to let app start first and bring up db driver.
   */
  lazy val db = new MongoDatabase

}

