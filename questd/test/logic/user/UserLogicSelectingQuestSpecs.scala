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

  /**
   * Creates stub config for our tests.
   */
  private def createStubConfig = {
    api.ConfigParams returns _ConfigParams

    val config = mock[ConfigSection]

    config.apply(api.ConfigParams.QuestProbabilityLevelsToGiveStartingQuests) returns "5"
    config.apply(api.ConfigParams.QuestProbabilityStartingVIPQuests) returns "0.50"

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
      approveReward = Assets(1, 2, 3),
      info = QuestInfo(
        authorId = authorid,
        themeId = "theme_id",
        content = QuestInfoContent(
          media = ContentReference(ContentType.Video, "", ""),
          icon = None,
          description = "The description"),
        vip = false))
  }

  "User Logic" should {

    "Return quest from friends if dice rolls so" in {

      api.config returns createStubConfig
      rand.nextDouble returns 0.13

      val qid = "qid"

      api.getFriendsQuests(any[GetFriendsQuestsRequest]) returns OkApiResult(GetFriendsQuestsResult(List(createQuest(qid, "author")).iterator))

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

      api.getShortlistQuests(any[GetShortlistQuestsRequest]) returns OkApiResult(GetShortlistQuestsResult(List(createQuest(qid, "author")).iterator))

      val u = User()
      val q = u.getRandomQuestForSolution

      there was one(rand).nextDouble
      there was one(api).getShortlistQuests(any[GetShortlistQuestsRequest])

      q must beSome.which(q => q.id == qid)
    }

    "Return liked quest if dice rolls so for solving" in {
      api.config returns createStubConfig
      rand.nextDouble returns 0.58

      val qid = "qid"

      api.getLikedQuests(any[GetLikedQuestsRequest]) returns OkApiResult(GetLikedQuestsResult(List(createQuest(qid, "author")).iterator))

      val u = User()
      val q = u.getRandomQuestForSolution

      there was one(rand).nextDouble
      there was one(api).getLikedQuests(any[GetLikedQuestsRequest])

      q must beSome.which(q => q.id == qid)
    }

    "Do not return liked quest if dice rolls so for voting" in {
      api.config returns createStubConfig
      rand.nextDouble returns 0.58

      val qid = "qid"

      api.getAllQuests(any[GetAllQuestsRequest]) returns OkApiResult(GetAllQuestsResult(List(createQuest(qid, "author")).iterator))

      val u = User()
      val q = u.getQuestProposalToVote

      there was one(rand).nextDouble
      there was no(api).getLikedQuests(any[GetLikedQuestsRequest])
      there was one(api).getAllQuests(any[GetAllQuestsRequest])

      q must beSome.which(q => q.id == qid)
    }

    "Return VIP quest if dice rolls so" in {
      api.config returns createStubConfig
      rand.nextDouble returns 0.75

      val qid = "qid"

      api.getVIPQuests(any[GetVIPQuestsRequest]) returns OkApiResult(GetVIPQuestsResult(List(createQuest(qid, "author")).iterator))

      val u = User()
      val q = u.getRandomQuestForSolution

      there was one(rand).nextDouble
      there was one(api).getVIPQuests(any[GetVIPQuestsRequest])

      q must beSome.which(q => q.id == qid)
    }

    "Return VIP quest with favorite theme ids if dice rolls so" in {
      val qid = "qid"
      val u = User(
        profile = Profile(
          publicProfile = PublicProfile(level = 10)),
        history = UserHistory(
          themesOfSelectedQuests = List("1", "2", "3", "4")))

      api.config returns createStubConfig
      rand.nextDouble returns 0.75
      rand.nextInt(4) returns 0 thenReturns 1 thenReturns 2

      api.getVIPQuests(GetVIPQuestsRequest(u, QuestStatus.InRotation, Some((-10, 11)), List("1", "2", "3"))) returns OkApiResult(GetVIPQuestsResult(List(createQuest(qid, "author")).iterator))

      val q = u.getRandomQuestForSolution

      there was one(rand).nextDouble
      there were three(rand).nextInt(4)
      there was one(api).getVIPQuests(GetVIPQuestsRequest(u, QuestStatus.InRotation, Some((-10, 11)), List("1", "2", "3")))

      q must beSome.which(q => q.id == qid)
    }

    "Return All quest with favorite theme ids if dice rolls so" in {
      val qid = "qid"
      val u = User(
        history = UserHistory(
          themesOfSelectedQuests = List("1", "2", "3", "4")))

      api.config returns createStubConfig
      rand.nextDouble returns 0.95
      rand.nextInt(4) returns 1

      api.getAllQuests(GetAllQuestsRequest(QuestStatus.InRotation, Some((-2, 19)), List("2"))) returns OkApiResult(GetAllQuestsResult(List(createQuest(qid, "author")).iterator))

      val q = u.getRandomQuestForSolution

      there was one(rand).nextDouble
      there was one(rand).nextInt(4)
      there was one(api).getAllQuests(GetAllQuestsRequest(QuestStatus.InRotation, Some((-2, 19)), List("2")))

      q must beSome.which(q => q.id == qid)
    }

    "Starting quests return vip quests" in {
      val qid = "qid"
      val u = User(
        profile = Profile(
          publicProfile = PublicProfile(level = 1)))

      api.config returns createStubConfig
      rand.nextDouble returns 0.0

      api.getVIPQuests(any[GetVIPQuestsRequest]) returns OkApiResult(GetVIPQuestsResult(List(createQuest(qid, "author")).iterator))

      u.getRandomQuestForSolution

      there was one(rand).nextDouble
      there was one(api).getVIPQuests(any[GetVIPQuestsRequest])
    }

    "Starting quests return other quests" in {
      val qid = "qid"
      val u = User(
        profile = Profile(
          publicProfile = PublicProfile(level = 1)))

      api.config returns createStubConfig
      rand.nextDouble returns 1.0

      api.getAllQuests(any[GetAllQuestsRequest]) returns OkApiResult(GetAllQuestsResult(List(createQuest(qid, "author")).iterator))

      u.getRandomQuestForSolution

      there was one(rand).nextDouble
      there was one(api).getAllQuests(any[GetAllQuestsRequest])
    }

    "Starting quests does not return other quests ignoring recent quests list if no quests available otherwise" in {
      val qid = "qid"
      val u = User(
        profile = Profile(
          publicProfile = PublicProfile(level = 1)),
        history = UserHistory(
          solvedQuestIds = List(List(qid))))

      api.config returns createStubConfig
      rand.nextDouble returns 1.0 thenReturns 1.0

      api.getAllQuests(any[GetAllQuestsRequest]) returns
        OkApiResult(GetAllQuestsResult(List(createQuest(qid, "author")).iterator)) thenReturns
        OkApiResult(GetAllQuestsResult(List(createQuest(qid, "author")).iterator)) thenReturns
        OkApiResult(GetAllQuestsResult(List(createQuest(qid, "author")).iterator)) thenReturns
        OkApiResult(GetAllQuestsResult(List(createQuest(qid, "author")).iterator))

      val q = u.getRandomQuestForSolution

      there were one(rand).nextDouble
      there were atLeast(3)(api).getAllQuests(any[GetAllQuestsRequest])
      q must beNone
    }

    "Other quests are used if vip quests are unavailable" in {
      val qid = "qid"
      val u = User()

      api.config returns createStubConfig
      rand.nextDouble returns 0.75

      api.getVIPQuests(any[GetVIPQuestsRequest]) returns OkApiResult(GetVIPQuestsResult(List().iterator))
      api.getAllQuests(any[GetAllQuestsRequest]) returns OkApiResult(GetAllQuestsResult(List(createQuest(qid, "author")).iterator))

      u.getRandomQuestForSolution

      there was one(rand).nextDouble
      there was one(api).getVIPQuests(any[GetVIPQuestsRequest])
      there was one(api).getAllQuests(any[GetAllQuestsRequest])
    }

    "All quests are used if vip and Other quests are unavailable" in {
      val qid = "qid"
      val u = User()

      api.config returns createStubConfig
      rand.nextDouble returns 0.75

      api.getVIPQuests(any[GetVIPQuestsRequest]) returns OkApiResult(GetVIPQuestsResult(List().iterator))
      api.getAllQuests(any[GetAllQuestsRequest]) returns OkApiResult(GetAllQuestsResult(List().iterator)) thenReturns OkApiResult(GetAllQuestsResult(List(createQuest(qid, "author")).iterator))

      u.getRandomQuestForSolution

      there was one(rand).nextDouble
      there was one(api).getVIPQuests(any[GetVIPQuestsRequest])
      there were two(api).getAllQuests(any[GetAllQuestsRequest])
    }

    "Quests for voting are worked out correctly" in {
      val qid = "qid"
      val u = User(
        history = UserHistory(
          themesOfSelectedQuests = List("1", "2", "3", "4")))

      api.config returns createStubConfig
      rand.nextDouble returns 0.95
      rand.nextInt(4) returns 1

      api.getAllQuests(GetAllQuestsRequest(
        QuestStatus.OnVoting,
        None,
        List("2"))) returns OkApiResult(GetAllQuestsResult(List(createQuest(qid, "author")).iterator))

      val q = u.getQuestProposalToVote

      there was one(rand).nextDouble
      there was one(rand).nextInt(4)
      there was one(api).getAllQuests(GetAllQuestsRequest(
        QuestStatus.OnVoting,
        None,
        List("2")))

      q must beSome.which(q => q.id == qid)
    }

  }
}

