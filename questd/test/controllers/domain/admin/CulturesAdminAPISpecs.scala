package controllers.domain.admin

import controllers.domain._
import models.domain._

class CulturesAdminAPISpecs extends BaseAPISpecs {

  "Cultures Admin API" should {

    "Merge cultures" in context {

      val oddCulture = Culture(name = "odd", countries = List("c1", "c2"))
      val coolCulture = Culture(name = "cool", countries = List("c3", "c4"))

      db.culture.readById(coolCulture.id) returns Some(coolCulture)
//      db.culture.update(coolCulture)
//      db.theme.replaceCultureIds(oldCultureId = oddCulture.id, newCultureId = request.idToMergeTo)
//      db.quest.replaceCultureIds(oldCultureId = oddCulture.id, newCultureId = request.idToMergeTo)
//      db.solution.replaceCultureIds(oldCultureId = oddCulture.id, newCultureId = request.idToMergeTo)
//      db.user.replaceCultureIds(oldCultureId = oddCulture.id, newCultureId = request.idToMergeTo)
//      db.culture.delete(request.id) // for new one.

      val result = api.mergeCultureIntoCulture(MergeCultureIntoCultureRequest(oddCulture, coolCulture.id))

      there was one(culture).readById(coolCulture.id)
      there was one(culture).update(coolCulture.copy(countries = coolCulture.countries ::: oddCulture.countries))
      there was one(theme).replaceCultureIds(oldCultureId = oddCulture.id, newCultureId = coolCulture.id)
      there was one(quest).replaceCultureIds(oldCultureId = oddCulture.id, newCultureId = coolCulture.id)
      there was one(solution).replaceCultureIds(oldCultureId = oddCulture.id, newCultureId = coolCulture.id)
      there was one(user).replaceCultureIds(oldCultureId = oddCulture.id, newCultureId = coolCulture.id)
      // TODO: check for replacing culture id in battles as well.
      there was one(culture).delete(oddCulture.id)

      result must beEqualTo(OkApiResult(MergeCultureIntoCultureResult()))
    }
  }
}

