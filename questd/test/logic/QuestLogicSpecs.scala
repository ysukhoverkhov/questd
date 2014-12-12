package logic

import controllers.domain.config._ConfigParams
import models.domain.Assets
import models.domain.admin.ConfigSection

class QuestLogicSpecs extends BaseLogicSpecs {

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

  "Quest logic should" should {

    "Calculate correct cost of quest to solve" in {
      api.config returns createStubConfig

      QuestLogic.costOfSolvingQuest(3) must beEqualTo(Assets(coins = 100))
      QuestLogic.costOfSolvingQuest(8) must beEqualTo(Assets(coins = 594))
      QuestLogic.costOfSolvingQuest(13) must beEqualTo(Assets(coins = 2060))
      QuestLogic.costOfSolvingQuest(20) must beEqualTo(Assets(coins = 10000))
    }
  }
}

