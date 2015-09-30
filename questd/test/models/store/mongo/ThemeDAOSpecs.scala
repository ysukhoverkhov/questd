package models.store.mongo

import models.domain.tag.Theme
import org.specs2.mutable._
import play.api.test.WithApplication
import testhelpers.domainstubs._

//@RunWith(classOf[JUnitRunner])
class ThemeDAOSpecs extends BaseDAOSpecs {

  private[this] def clearDB() = {
    db.theme.clear()
  }

  "Mongo Theme DAO" should {
    "Filter theme by culture" in new WithApplication(appWithTestDatabase) {
      clearDB()

      val t1 = createThemeStub(id = "rus", cultureId = "rus")
      val t2 = createThemeStub(id = "eng", cultureId = "eng")

      db.theme.create(t1)
      db.theme.create(t2)

      val cultures = db.theme.allWithParams(cultureId = Some(t1.cultureId)).toList
      cultures.map(_.id) must beEqualTo(List(t1.id))
    }

    "Replace cultures" in new WithApplication(appWithTestDatabase) {
      clearDB()

      val t1 = createThemeStub(id = "id1", cultureId = "rus")
      val t2 = createThemeStub(id = "id2", cultureId = "eng")
      val t3 = createThemeStub(id = "id3", cultureId = "rus")

      db.theme.create(t1)
      db.theme.create(t2)
      db.theme.create(t3)

      db.theme.replaceCultureIds("rus", "eng")

      val ou1 = db.theme.readById(t1.id)
      ou1 must beSome.which((u: Theme) => u.id == t1.id)
      ou1 must beSome.which((u: Theme) => u.cultureId == "eng")

      val ou2 = db.theme.readById(t2.id)
      ou2 must beSome.which((u: Theme) => u.id == t2.id)
      ou2 must beSome.which((u: Theme) => u.cultureId == "eng")

      val ou3 = db.theme.readById(t3.id)
      ou3 must beSome.which((u: Theme) => u.id == t3.id)
      ou3 must beSome.which((u: Theme) => u.cultureId == "eng")
    }

  }
}

