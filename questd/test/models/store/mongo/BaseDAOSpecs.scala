package models.store.mongo

import org.specs2.mutable._
import play.api.test.FakeApplication

trait BaseDAOSpecs
  extends Specification
  with MongoDatabaseComponent {

  isolated

  def testMongoDatabase(name: String = "default"): Map[String, String] = {
    val dbname: String = "questdb-test-0-40"
    Map(
      "mongodb." + name + ".db" -> dbname)
  }
  val appWithTestDatabase = FakeApplication(additionalConfiguration = testMongoDatabase())

  /*
   * Initializing components. It's lazy to let app start first and bring up db driver.
   */
  lazy val db = new MongoDatabase
}

