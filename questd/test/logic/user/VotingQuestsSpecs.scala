package logic.user

import controllers.domain.app.protocol.ProfileModificationResult
import controllers.domain.config._ConfigParams
import logic.BaseLogicSpecs
import models.domain.{ContentVote, Rights}
import models.domain.admin.ConfigSection
import testhelpers.domainstubs._

class VotingQuestsSpecs extends BaseLogicSpecs {

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
    config.apply(api.ConfigParams.QuestProbabilityStar) returns "0.10"

    config.apply(api.ConfigParams.ProposalMaxDescriptionLength) returns "100"

    config
  }

  "User Logic for voting for quests" should {

    "Do not allow voting for quests without rights" in {
      api.config returns createStubConfig

      val user = createUserStub(rights = Rights.none)
      val q = createQuestStub()

      val rv = user.canVoteQuest(q.id)

      rv must beEqualTo(ProfileModificationResult.NotEnoughRights)
    }

    "Do not allow voting for quests not in time line" in {
      api.config returns createStubConfig

      val user = createUserStub()
      val q = createQuestStub()

      val rv = user.canVoteQuest(q.id)

      rv must beEqualTo(ProfileModificationResult.OutOfContent)
    }

    "Do not allow voting for quests in time line but we already voted for" in {
      api.config returns createStubConfig

      val q = createQuestStub()
      val user = createUserStub(timeLine = List(createTimeLineEntryStub(objectId = q.id, ourVote = Some(ContentVote.Cool))))

      val rv = user.canVoteQuest(q.id)

      rv must beEqualTo(ProfileModificationResult.InvalidState)
    }

    "Do not allow voting for own quests" in {
      api.config returns createStubConfig

      val userId = "userId"
      val q = createQuestStub()
      val user = createUserStub(
        id = userId,
        timeLine = List(createTimeLineEntryStub(objectId = q.id, objectAuthorId = userId)))

      val rv = user.canVoteQuest(q.id)

      rv must beEqualTo(ProfileModificationResult.OutOfContent)
    }

    "Allow voting for quests in normal situations" in {
      api.config returns createStubConfig

      val q = createQuestStub()
      val user = createUserStub(timeLine = List(createTimeLineEntryStub(objectId = q.id)))

      val rv = user.canVoteQuest(q.id)

      rv must beEqualTo(ProfileModificationResult.OK)
    }
  }
}

