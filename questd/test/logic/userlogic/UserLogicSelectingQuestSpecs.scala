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
import java.util.Date

class UserLogicSelectingQuestSpecs extends BaseUserLogicSpecs {

  isolated

  /**
   * Creates 10 themes for mocking
   */
  private def createStubThemes: List[Theme] = {
    (for (i <- List.range(1, 11)) yield {
      Theme(text = i.toString, comment = i.toString)
    })
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
  private def createUser(friends: List[Friendship]) = {
    User(friends = friends)
  }

  
  private def createFriend(newid: String) = {
    User(id = newid)
  }
  
  private def createQuest(newid: String, authorid: String) = {
    Quest(
        themeID = "theme_id",
        authorUserID = authorid,
        approveReward = Assets(1, 2, 3),
        info = QuestInfo(QuestInfoContent(media = ContentReference("", "", ""), icon = None, description = "descr")))
  }
  
  
  "User Logic" should {
    
    "Return quest from friends if dice rolls so" in {
//      val q = u.getRandomQuestForSolution
      success
    }

    "Return quest from shortlist if dice rolls so" in {
      success
    }
    
  }
}


// TODO: test each option out of posible quest slution options is selectable. 
