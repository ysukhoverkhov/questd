package logic.user

import controllers.domain.app.protocol.ProfileModificationResult
import controllers.domain.config._ConfigParams
import logic.BaseLogicSpecs
import models.domain.{Assets, ContentType, Rights}
import models.domain.admin.ConfigSection
import testhelpers.domainstubs._

class SolvingQuestSpecs extends BaseLogicSpecs {

  /**
   * Creates stub config for our tests.
   */
  private def createStubConfig = {
    api.ConfigParams returns _ConfigParams

    val config = mock[ConfigSection]

    config.apply(api.ConfigParams.QuestProbabilityLevelsToGiveStartingQuests) returns "5"
    config.apply(api.ConfigParams.QuestProbabilityStartingVIPQuests) returns "0.50"

    config.apply(api.ConfigParams.QuestProbabilityFriends) returns "0.25"
    config.apply(api.ConfigParams.QuestProbabilityFollowing) returns "0.25"
    config.apply(api.ConfigParams.QuestProbabilityLiked) returns "0.20"
    config.apply(api.ConfigParams.QuestProbabilityVIP) returns "0.10"

    config.apply(api.ConfigParams.ProposalMaxDescriptionLength) returns "100"

    config
  }

  "User Logic for solving quests" should {

    "Do not allow solving of quests without rights" in {
      api.config returns createStubConfig

      val user = createUserStub(rights = Rights.none)
      val q = createQuestStub()

      val rv = user.canSolveQuest(ContentType.Photo, q)

      rv must beEqualTo(ProfileModificationResult.NotEnoughRights)
    }

    "Do not allow solving of quests without money" in {
      api.config returns createStubConfig

      val q = createQuestStub(solveCost = Assets(100, 0, 0))
      val tl = List(createTimeLineEntryStub(objectId = q.id))
      val user = createUserStub(assets = Assets(), timeLine = tl)

      val rv = user.canSolveQuest(ContentType.Photo, q)

      rv must beEqualTo(ProfileModificationResult.NotEnoughAssets)
    }

    "Do not allow solving of quests not in our time line" in {
      api.config returns createStubConfig

      val user = createUserStub()
      val q = createQuestStub()

      val rv = user.canSolveQuest(ContentType.Photo, q)

      rv must beEqualTo(ProfileModificationResult.OutOfContent)
    }

    "Do not allow solving of own quests" in {
      api.config returns createStubConfig

      val questId = "qid"
      val tl = List(createTimeLineEntryStub(objectId = questId))
      val user = createUserStub(timeLine = tl)
      val q = createQuestStub(id = questId, authorId = user.id)

      val rv = user.canSolveQuest(ContentType.Photo, q)

      rv must beEqualTo(ProfileModificationResult.InvalidState)
    }

    "Do not allow solving of quests already solved" in {
      api.config returns createStubConfig

      val questId = "qid"
      val tl = List(createTimeLineEntryStub(objectId = questId))
      val user = createUserStub(timeLine = tl, solvedQuests = List(questId))
      val q = createQuestStub(id = questId)

      val rv = user.canSolveQuest(ContentType.Photo, q)

      rv must beEqualTo(ProfileModificationResult.InvalidState)
    }

    "Allow creating of quests in normal situations" in {
      api.config returns createStubConfig

      val q = createQuestStub(solveCost = Assets(100, 0, 0))
      val tl = List(createTimeLineEntryStub(objectId = q.id))
      val user = createUserStub(assets = Assets(100, 0, 0), timeLine = tl)

      val rv = user.canSolveQuest(ContentType.Photo, q)

      rv must beEqualTo(ProfileModificationResult.OK)
    }
  }
}

