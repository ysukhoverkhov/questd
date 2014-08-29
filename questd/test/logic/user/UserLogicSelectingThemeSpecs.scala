package logic.user

import controllers.domain.OkApiResult
import controllers.domain.admin._
import controllers.domain.app.theme.GetAllThemesForCultureResult
import controllers.domain.config._ConfigParams
import models.domain._
import models.domain.admin.ConfigSection
import models.domain.stubCreators._

class UserLogicSelectingThemeSpecs extends BaseUserLogicSpecs {

  /**
   * Creates 10 themes for mocking
   */
  private def createStubThemes: List[Theme] = {
    for (i <- List.range(1, 11)) yield {
      createThemeStub(name = i.toString, desc = i.toString)
    }
  }

  /**
   * Creates stub config for our tests.
   */
  private def createStubConfig = {
    api.ConfigParams returns _ConfigParams

    val config = mock[ConfigSection]
    config.apply(api.ConfigParams.FavoriteThemesShare) returns "0.20"
    config.apply(api.ConfigParams.FavoriteThemesProbability) returns "0.75"
    config
  }

  /**
   * Creates user we will test algorithm with
   */
  private def createUser(themes: List[Theme], favTheme: Int) = {
    User(
      demo = UserDemographics(cultureId = Some("cultureId")),
      history = UserHistory(
        selectedThemeIds = List(themes(favTheme).id, themes(1).id)))
  }

  "User Logic" should {

    "Return correct theme from favorite" in {

      val themes = createStubThemes
      val favTheme = 4

      def fillMocks = {
        api.allThemes(AllThemesRequest(sorted = true)) returns OkApiResult(AllThemesResult(themes.iterator))
        api.getTheme(GetThemeRequest(themes(favTheme).id)) returns OkApiResult(GetThemeResult(themes(favTheme)))

        api.config returns createStubConfig

        rand.nextDouble returns 0.013
        rand.nextInt(anyInt) returns 0
      }

      fillMocks

      val u = createUser(themes, favTheme)
      val ot = u.getRandomThemeForQuestProposal(createStubThemes.length)
      there was one(rand).nextDouble
      there was one(rand).nextInt(anyInt)
      ot must beSome.which((t: Theme) => t.id == themes(favTheme).id)
    }

    "Return correct theme from global" in {

      val themes = createStubThemes

      def fillMocks = {
        api.getAllThemesForCulture(any) returns OkApiResult(GetAllThemesForCultureResult(themes.iterator))
        api.getTheme(GetThemeRequest(themes(0).id)) returns OkApiResult(GetThemeResult(themes(0)))

        api.config returns createStubConfig

        rand.nextDouble returns 1.0
        rand.nextInt(anyInt) returns 0
      }

      fillMocks

      val u = createUser(themes, 4)
      val ot = u.getRandomThemeForQuestProposal(createStubThemes.length)
      there was one(rand).nextDouble
      ot must beSome.which((t: Theme) => t.id == themes(0).id)
    }

    "Return None if no themes in db" in {

      def fillMocks = {
        api.getAllThemesForCulture(any) returns OkApiResult(GetAllThemesForCultureResult(List[Theme]().iterator))

        api.config returns createStubConfig

        rand.nextDouble returns 0.2
      }

      fillMocks

      User().getRandomThemeForQuestProposal(10) must beNone
    }

  }
}


