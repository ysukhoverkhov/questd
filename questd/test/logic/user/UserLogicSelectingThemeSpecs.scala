package logic.user

import logic.BaseLogicSpecs
import models.domain._
import testhelpers.domainstubs._

class UserLogicSelectingThemeSpecs extends BaseLogicSpecs {

  /**
   * Creates 10 themes for mocking
   */
  private def createStubThemes: List[Theme] = {
    for (i <- List.range(1, 11)) yield {
      createThemeStub(name = i.toString, desc = i.toString)
    }
  }

  "User Logic" should {

    // TODO: clean me up with tags.
//    "Return correct theme from favorite" in {
//
//      val themes = createStubThemes
//      val favTheme = 4
//
//      def fillMocks = {
//        api.getAllThemesForCulture(any[GetAllThemesForCultureRequest]) returns OkApiResult(GetAllThemesForCultureResult(themes.iterator))
//        api.allThemes(AllThemesRequest(sorted = true)) returns OkApiResult(AllThemesResult(themes.iterator))
//        api.getTheme(GetThemeRequest(themes(favTheme).id)) returns OkApiResult(GetThemeResult(themes(favTheme)))
//
//        api.config returns createStubConfig
//
//        rand.nextDouble returns 0.013
//        rand.nextInt(anyInt) returns 0
//      }
//
//      fillMocks
//
//      val u = createUserStub(favThemes = List(themes(favTheme).id, themes(1).id))
//      val ot = u.getRandomThemeForQuestProposal(createStubThemes.length)
//      there was one(rand).nextDouble
//      there was one(rand).nextInt(anyInt)
//      ot must beSome.which((t: Theme) => t.id == themes(favTheme).id)
//    }

//    "Return correct theme from global" in {
//
//      val themes = createStubThemes
//
//      def fillMocks = {
//        api.getAllThemesForCulture(any) returns OkApiResult(GetAllThemesForCultureResult(themes.iterator))
//        api.getTheme(GetThemeRequest(themes(0).id)) returns OkApiResult(GetThemeResult(themes(0)))
//
//        api.config returns createStubConfig
//
//        rand.nextDouble returns 1.0
//        rand.nextInt(anyInt) returns 0
//      }
//
//      fillMocks
//
//      val u = createUserStub(favThemes = List(themes(4).id, themes(1).id))
//      val ot = u.getRandomThemeForQuestProposal(createStubThemes.length)
//      there was one(rand).nextDouble
//      ot must beSome.which((t: Theme) => t.id == themes(0).id)
//    }

//    "Return None if no themes in db" in {
//      def fillMocks = {
//        api.getAllThemesForCulture(any) returns OkApiResult(GetAllThemesForCultureResult(List[Theme]().iterator))
//
//        api.config returns createStubConfig
//
//        rand.nextDouble returns 0.2
//      }
//
//      fillMocks
//
//      createUserStub().getRandomThemeForQuestProposal(10) must beNone
//    }

  }
}

