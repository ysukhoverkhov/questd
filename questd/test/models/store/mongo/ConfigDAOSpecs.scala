

package models.store.mongo

import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import play.api.test._
import play.api.test.Helpers._
import play.Logger
import models.store._
import models.domain.admin._
import com.mongodb.casbah.commons.MongoDBObject

//@RunWith(classOf[JUnitRunner])
class ConfigDAOSpecs extends Specification
  with MongoDatabaseComponent
  with BaseDAOSpecs {

  "Mongo Config DAO" should {
    "Return empty configuration" in new WithApplication(appWithTestDatabase) {
      val c = db.config.readConfig
      c("aa") must beNone
    }

    "Add config section to configuration" in new WithApplication(appWithTestDatabase) {

      val cs1 = ConfigSection("sec1", Map(("k1" -> "v1"), ("k2" -> "v2")))
      val cs2 = ConfigSection("sec2", Map(("k3" -> "v3"), ("k4" -> "v4"), ("k5" -> "v5")))
      db.config.upsertSection(cs1)
      db.config.upsertSection(cs1)
      db.config.upsertSection(cs2)

      db.config.dao.collection.ensureIndex(MongoDBObject("name" -> 1), "names_index", true)

      val c = db.config.readConfig
      c("sec1") must beSome.which((s: ConfigSection) => s.name == cs1.name)
      c("sec1") must beSome.which((s: ConfigSection) => s.values("k1") == cs1.values("k1"))
      c("sec1") must beSome.which((s: ConfigSection) => s.values.size == cs1.values.size)

      c("sec2") must beSome.which((s: ConfigSection) => s.name == cs2.name)
      c("sec2") must beSome.which((s: ConfigSection) => s.values("k4") == cs2.values("k4"))
      c("sec2") must beSome.which((s: ConfigSection) => s.values.size == cs2.values.size)
    }

    "Update config section in configuration" in new WithApplication(appWithTestDatabase) {

      val cs1 = ConfigSection("sec1", Map(("k1" -> "v1"), ("k2" -> "v2")))
      val cs2 = ConfigSection("sec1", Map(("k3" -> "v3"), ("k4" -> "v4"), ("k5" -> "v5")))
      db.config.upsertSection(cs1)
      db.config.dao.collection.ensureIndex(MongoDBObject("name" -> 1), "names_index", true)

      val c = db.config.readConfig
      c("sec1") must beSome.which((s: ConfigSection) => s.name == cs1.name)
      c("sec1") must beSome.which((s: ConfigSection) => s.values("k1") == cs1.values("k1"))
      c("sec1") must beSome.which((s: ConfigSection) => s.values.size == cs1.values.size)

      db.config.upsertSection(cs2)

      val c2 = db.config.readConfig
      c2("sec1") must beSome.which((s: ConfigSection) => s.name == cs2.name)
      c2("sec1") must beSome.which((s: ConfigSection) => s.values("k4") == cs2.values("k4"))
      c2("sec1") must beSome.which((s: ConfigSection) => s.values.size == cs2.values.size)
    }

    "Delete config section from configuration" in new WithApplication(appWithTestDatabase) {
      val cs1 = ConfigSection("sec1", Map(("k1" -> "v1"), ("k2" -> "v2")))
      db.config.upsertSection(cs1)
      db.config.deleteSection("sec1")
      val c = db.config.readConfig
      c("sec1") must beNone
    }

  }

}

