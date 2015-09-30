

package models.store.mongo

import models.domain.admin._
import play.api.test._

//@RunWith(classOf[JUnitRunner])
class ConfigDAOSpecs extends BaseDAOSpecs {

  "Mongo Config DAO" should {
    "Return empty configuration" in new WithApplication(appWithTestDatabase) {
      val c = db.config.readConfig
      c("aa") must beNone
    }

    "Add config section to configuration" in new WithApplication(appWithTestDatabase) {

      val cs1 = ConfigSection("sec1", Map("k1" -> "v1", "k2" -> "v2"))
      val cs2 = ConfigSection("sec2", Map("k3" -> "v3", "k4" -> "v4", "k5" -> "v5"))
      db.config.upsert(cs1)
      db.config.upsert(cs1)
      db.config.upsert(cs2)

      val c = db.config.readConfig
      c("sec1") must beSome.which((s: ConfigSection) => s.id == cs1.id)
      c("sec1") must beSome.which((s: ConfigSection) => s.values("k1") == cs1.values("k1"))
      c("sec1") must beSome.which((s: ConfigSection) => s.values.size == cs1.values.size)

      c("sec2") must beSome.which((s: ConfigSection) => s.id == cs2.id)
      c("sec2") must beSome.which((s: ConfigSection) => s.values("k4") == cs2.values("k4"))
      c("sec2") must beSome.which((s: ConfigSection) => s.values.size == cs2.values.size)
    }

    "Update config section in configuration" in new WithApplication(appWithTestDatabase) {

      val cs1 = ConfigSection("sec1", Map("k1" -> "v1", "k2" -> "v2"))
      val cs2 = ConfigSection("sec1", Map("k3" -> "v3", "k4" -> "v4", "k5" -> "v5"))
      db.config.upsert(cs1)

      val c = db.config.readConfig
      c("sec1") must beSome.which((s: ConfigSection) => s.id == cs1.id)
      c("sec1") must beSome.which((s: ConfigSection) => s.values("k1") == cs1.values("k1"))
      c("sec1") must beSome.which((s: ConfigSection) => s.values.size == cs1.values.size)

      db.config.upsert(cs2)

      val c2 = db.config.readConfig
      c2("sec1") must beSome.which((s: ConfigSection) => s.id == cs2.id)
      c2("sec1") must beSome.which((s: ConfigSection) => s.values("k4") == cs2.values("k4"))
      c2("sec1") must beSome.which((s: ConfigSection) => s.values.size == cs2.values.size)
    }

    "Delete config section from configuration" in new WithApplication(appWithTestDatabase) {
      val cs1 = ConfigSection("sec1", Map("k1" -> "v1", "k2" -> "v2"))
      db.config.upsert(cs1)
      db.config.delete("sec1")
      val c = db.config.readConfig
      c("sec1") must beNone
    }

  }

}

