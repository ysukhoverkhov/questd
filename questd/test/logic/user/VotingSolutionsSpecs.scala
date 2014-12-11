package logic.user

import controllers.domain.app.protocol.ProfileModificationResult
import controllers.domain.config._ConfigParams
import logic.BaseLogicSpecs
import models.domain.admin.ConfigSection
import models.domain.{ContentVote, Rights}
import testhelpers.domainstubs._

class VotingSolutionsSpecs extends BaseLogicSpecs {

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

  "User Logic for voting for solutions" should {

    "Do not allow voting for solutions without rights" in {
      api.config returns createStubConfig

      val user = createUserStub(rights = Rights.none)
      val s = createSolutionStub()

      val rv = user.canVoteSolution(s.id)

      rv must beEqualTo(ProfileModificationResult.NotEnoughRights)
    }

    "Do not allow voting for solutions not in time line" in {
      api.config returns createStubConfig

      val user = createUserStub()
      val s = createSolutionStub()

      val rv = user.canVoteSolution(s.id)

      rv must beEqualTo(ProfileModificationResult.OutOfContent)
    }

    "Do not allow voting for solutions in time line but we already voted for" in {
      api.config returns createStubConfig

      val s = createSolutionStub()
      val user = createUserStub(timeLine = List(createTimeLineEntryStub(objectId = s.id, ourVote = Some(ContentVote.Cool))))

      val rv = user.canVoteSolution(s.id)

      rv must beEqualTo(ProfileModificationResult.InvalidState)
    }

    "Do not allow voting for solutions created by us" in {
      api.config returns createStubConfig

      val uid = "uid"
      val s = createSolutionStub()
      val user = createUserStub(id = uid, timeLine = List(createTimeLineEntryStub(objectId = s.id, objectAuthorId = uid)))

      val rv = user.canVoteSolution(s.id)

      rv must beEqualTo(ProfileModificationResult.OutOfContent)
    }

    "Do not allow voting solutions with incomplete bio" in {
      api.config returns createStubConfig

      val s = createSolutionStub()
      val user = createUserStub(cultureId = "")

      val rv = user.canVoteSolution(s.id)

      rv must beEqualTo(ProfileModificationResult.OutOfContent)
    }

    "Allow voting for solutions in normal situations" in {
      api.config returns createStubConfig

      val s = createSolutionStub()
      val user = createUserStub(timeLine = List(createTimeLineEntryStub(objectId = s.id)))

      val rv = user.canVoteSolution(s.id)

      rv must beEqualTo(ProfileModificationResult.OK)
    }
  }
}

