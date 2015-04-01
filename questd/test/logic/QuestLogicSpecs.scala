package logic

import models.domain.Assets

class QuestLogicSpecs extends BaseLogicSpecs {

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
