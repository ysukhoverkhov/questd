

package models.store.mongo

import models.domain.Culture
import org.specs2.mutable._
import play.api.test._

//@RunWith(classOf[JUnitRunner])
class CultureDAOSpecs extends Specification
with MongoDatabaseComponent
with BaseDAOSpecs {

  private[this] def clearDB() = {
    db.culture.clear()
  }

  "Mongo Quest DAO" should {
    "Search culture by country" in new WithApplication(appWithTestDatabase) {
      clearDB()

      val id = "cultureId"
      val countries = List("c1", "c2", "c3")

      db.culture.create(Culture(
        name = "name_wrong",
        countries = List("lq1", "adasd")))
      db.culture.create(Culture(
        id = id,
        name = "name",
        countries = countries))
      db.culture.create(Culture(
        name = "name_wrong",
        countries = List("lq")))

      val c1 = db.culture.findByCountry(countries(1))
      val c2 = db.culture.findByCountry("")

      c1 must beSome[Culture]
      c1 must beSome.which((s: Culture) => s.id == id)
      c2 must beNone
    }
  }

}

