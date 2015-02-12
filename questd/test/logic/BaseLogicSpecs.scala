package logic

import components.{APIAccessor, RandomAccessor}
import components.random.RandomComponent
import controllers.domain.DomainAPIComponent
import controllers.domain.config._ConfigParams
import controllers.sn.component.SocialNetworkComponent
import models.domain.admin.ConfigSection
import models.store.DatabaseComponent
import org.specs2.mock.Mockito
import org.specs2.mutable._

private[logic] abstract class BaseLogicSpecs extends Specification
  with Mockito
  with LogicBootstrapper
  with APIAccessor
  with RandomAccessor

  with RandomComponent
  with DatabaseComponent
  with DomainAPIComponent
  with SocialNetworkComponent {

  isolated

  val sn = mock[SocialNetwork]
  val db = mock[Database]
  val api = mock[DomainAPI]
  val rand = mock[Random]

  /**
   * Creates stub config for our tests.
   */
  protected final def createStubConfig = {
    api.ConfigParams returns _ConfigParams

    val config = mock[ConfigSection]

    config.apply(api.ConfigParams.QuestProbabilityLevelsToGiveStartingQuests) returns "5"
    config.apply(api.ConfigParams.QuestProbabilityStartingVIPQuests) returns "0.50"
    config.apply(api.ConfigParams.QuestProbabilityFriends) returns "0.25"
    config.apply(api.ConfigParams.QuestProbabilityFollowing) returns "0.45"
    config.apply(api.ConfigParams.QuestProbabilityVIP) returns "0.10"

    config.apply(api.ConfigParams.SolutionProbabilityLevelsToGiveStartingSolutions) returns "5"
    config.apply(api.ConfigParams.SolutionProbabilityStartingVIPSolutions) returns "0.50"
    config.apply(api.ConfigParams.SolutionProbabilityFriends) returns "0.25"
    config.apply(api.ConfigParams.SolutionProbabilityFollowing) returns "0.25"
    config.apply(api.ConfigParams.SolutionProbabilityLiked) returns "0.20"
    config.apply(api.ConfigParams.SolutionProbabilityVIP) returns "0.10"

    config.apply(api.ConfigParams.BattleProbabilityLevelsToGiveStartingBattles) returns "5"
    config.apply(api.ConfigParams.BattleProbabilityStartingVIPBattles) returns "0.50"
    config.apply(api.ConfigParams.BattleProbabilityFriends) returns "0.25"
    config.apply(api.ConfigParams.BattleProbabilityFollowing) returns "0.25"
    config.apply(api.ConfigParams.BattleProbabilityLiked) returns "0.20"
    config.apply(api.ConfigParams.BattleProbabilityVIP) returns "0.10"

    config.apply(api.ConfigParams.ProposalMaxDescriptionLength) returns "100"

    config.apply(api.ConfigParams.SolutionVoteTaskCountMean) returns "3"
    config.apply(api.ConfigParams.SolutionVoteTaskCountDeviation) returns "1"
    config.apply(api.ConfigParams.CreateSolutionTaskProbability) returns "0.5"
    config.apply(api.ConfigParams.AddToFollowingTaskProbability) returns "0.3"
    config.apply(api.ConfigParams.QuestVoteTaskCountMean) returns "3"
    config.apply(api.ConfigParams.QuestVoteTaskCountDeviation) returns "1"
    config.apply(api.ConfigParams.CreateQuestTaskProbability) returns "0.3"

    config.apply(api.ConfigParams.FavoriteThemesShare) returns "0.20"
    config.apply(api.ConfigParams.FavoriteThemesProbability) returns "0.75"

    config
  }

}
