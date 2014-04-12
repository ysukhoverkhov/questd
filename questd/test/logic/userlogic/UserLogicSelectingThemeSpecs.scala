package logic.userlogic

import org.specs2.mutable._
import org.specs2.runner._
import org.specs2.mock.Mockito
import org.junit.runner._
import logic._
import controllers.domain.app.protocol.ProfileModificationResult._
import models.domain._
import org.joda.time.Hours
import components.APIAccessor
import controllers.domain.DomainAPIComponent
import models.store.DatabaseComponent
import components.random.RandomComponent
import components.RandomAccessor
import controllers.domain.admin._
import controllers.domain.app.user._
import controllers.domain.app.quest._
import controllers.domain.OkApiResult
import models.domain.admin.ConfigSection
import controllers.domain.DomainAPIComponent
import controllers.domain.config._ConfigParams
import com.github.nscala_time.time.Imports.DateTime
import com.github.nscala_time.time.Imports.richDateTime
import logic.LogicBootstrapper

class UserLogicSpecs extends BaseUserLogicSpecs {

  isolated 
 
  /**
   * Creates 10 themes for mocking
   */
  def createStubThemes: List[Theme] = {
    (for (i <- List.range(1, 11)) yield {
      Theme(text = i.toString, comment = i.toString)
    })
  }

  /**
   * Creates stub config for our tests.
   */
  def createStubConfig = {
    api.ConfigParams returns _ConfigParams

    val config = mock[ConfigSection]
    config.apply(api.ConfigParams.FavoriteThemesShare) returns "0.20"
    config.apply(api.ConfigParams.FavoriteThemesProbability) returns "0.75"
    config
  }

  /**
   * Creates user we will test algorithm with
   */
  def createUser(themes: List[Theme], favTheme: Int) = {
    User(history = UserHistory(selectedThemeIds = List(themes(favTheme).id, themes(1).id)))
  }

  "User Logic" should {

    "Return correct theme from favorite" in {

      val themes = createStubThemes
      val favTheme = 4
      
      def fillMocks = {
        api.allThemes(AllThemesRequest(sorted = true)) returns OkApiResult(Some(AllThemesResult(themes.iterator)))
        api.getTheme(GetThemeRequest(themes(favTheme).id)) returns OkApiResult(Some(GetThemeResult(themes(favTheme))))
        
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
        api.allThemes(AllThemesRequest(sorted = true)) returns OkApiResult(Some(AllThemesResult(themes.iterator)))
        api.getTheme(GetThemeRequest(themes(0).id)) returns OkApiResult(Some(GetThemeResult(themes(0))))
        
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
        api.allThemes(AllThemesRequest(sorted = true)) returns OkApiResult(Some(AllThemesResult(List[Theme]().iterator)))
        api.config returns createStubConfig
        rand.nextDouble returns 0.2
      }

      fillMocks

      User().getRandomThemeForQuestProposal(10) must beNone
    }

  }
}


