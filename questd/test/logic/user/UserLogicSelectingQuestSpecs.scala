package logic.user

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
    config.apply(api.ConfigParams.QuestProbabilityFriends) returns "0.25"
    config.apply(api.ConfigParams.QuestProbabilityShortlist) returns "0.25"
    config.apply(api.ConfigParams.QuestProbabilityLiked) returns "0.20"
    config.apply(api.ConfigParams.QuestProbabilityStar) returns "0.10"
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
      id = newid,
      themeID = "theme_id",
      authorUserID = authorid,
      approveReward = Assets(1, 2, 3),
      info = QuestInfo(QuestInfoContent(media = ContentReference("", "", ""), icon = None, description = "descr")))
  }

  "User Logic" should {

    "Return quest from friends if dice rolls so" in {

      api.config returns createStubConfig
      rand.nextDouble returns 0.13

      val qid = "qid"

      api.getFriendsQuests(any[GetFriendsQuestsRequest]) returns OkApiResult(Some(GetFriendsQuestsResult(List(createQuest(qid, "author")).iterator)))

      val u = User()
      val q = u.getRandomQuestForSolution

      there was one(rand).nextDouble
      there was one(api).getFriendsQuests(any[GetFriendsQuestsRequest])

      q must beSome.which(q => q.id == qid)
    }

    "Return quest from shortlist if dice rolls so" in {
      api.config returns createStubConfig
      rand.nextDouble returns 0.38

      val qid = "qid"

      api.getShortlistQuests(any[GetShortlistQuestsRequest]) returns OkApiResult(Some(GetShortlistQuestsResult(List(createQuest(qid, "author")).iterator)))

      val u = User()
      val q = u.getRandomQuestForSolution

      there was one(rand).nextDouble
      there was one(api).getShortlistQuests(any[GetShortlistQuestsRequest])

      q must beSome.which(q => q.id == qid)
    }

    "Return liked quest if dice rolls so" in {
      api.config returns createStubConfig
      rand.nextDouble returns 0.58

      val qid = "qid"

      api.getLikedQuests(any[GetLikedQuestsRequest]) returns OkApiResult(Some(GetLikedQuestsResult(List(createQuest(qid, "author")).iterator)))

      val u = User()
      val q = u.getRandomQuestForSolution

      there was one(rand).nextDouble
      there was one(api).getLikedQuests(any[GetLikedQuestsRequest])

      q must beSome.which(q => q.id == qid)
    }
    
    "Return VIP quest if dice rolls so" in {
      api.config returns createStubConfig
      rand.nextDouble returns 0.75

      val qid = "qid"

      api.getVIPQuests(any[GetVIPQuestsRequest]) returns OkApiResult(Some(GetVIPQuestsResult(List(createQuest(qid, "author")).iterator)))

      val u = User()
      val q = u.getRandomQuestForSolution

      there was one(rand).nextDouble
      there was one(api).getVIPQuests(any[GetVIPQuestsRequest])

      q must beSome.which(q => q.id == qid)
    }
  }
}


// TODO: test each option out of posible quest slution options is selectable. 