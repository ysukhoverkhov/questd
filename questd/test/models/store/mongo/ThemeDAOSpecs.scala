package models.store.mongo

import org.specs2.mutable._
import play.api.test.WithApplication
import testhelpers.domainstubs._

//@RunWith(classOf[JUnitRunner])
class ThemeDAOSpecs
  extends Specification
  with MongoDatabaseComponent
  with BaseDAOSpecs {

  private[this] def clearDB() = {
    db.theme.clear()
  }

  "Mongo Theme DAO" should {
    "Filter theme by culture " in new WithApplication(appWithTestDatabase) {
      clearDB()

      val t1 = createThemeStub(id = "rus", cultureId = "rus")
      val t2 = createThemeStub(id = "eng", cultureId = "eng")

      db.theme.create(t1)
      db.theme.create(t2)

      val cultures = db.theme.allWithParams(cultureId = Some(t1.cultureId)).toList
      cultures.map(_.id) must beEqualTo(List(t1.id))
    }
  }
}

