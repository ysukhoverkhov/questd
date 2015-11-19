package logic

import components.random.RandomComponent
import components.{APIAccessor, RandomAccessor}
import controllers.domain.DomainAPIComponent
import controllers.domain.config.{_DefaultConfigParams, _TutorialConfigParams}
import controllers.services.socialnetworks.component.SocialNetworkComponent
import models.domain.admin.ConfigSection
import models.store.DatabaseComponent
import models.store.dao.ChallengeDAO
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

  val challenge = mock[ChallengeDAO]

  /**
   * Creates stub config for our tests.
   */
  private final def createStubDefaultConfig = {
    api.DefaultConfigParams returns _DefaultConfigParams

    val configSection = mock[ConfigSection]

    configSection.apply(api.DefaultConfigParams.RequestsAutoRejectDays) returns "7"

    configSection.apply(api.DefaultConfigParams.QuestProbabilityLevelsToGiveStartingQuests) returns "5"
    configSection.apply(api.DefaultConfigParams.QuestProbabilityStartingVIPQuests) returns "0.50"
    configSection.apply(api.DefaultConfigParams.QuestProbabilityStartingFriendQuests) returns "0.20"
    configSection.apply(api.DefaultConfigParams.QuestProbabilityStartingFollowingQuests) returns "0.20"

    configSection.apply(api.DefaultConfigParams.QuestProbabilityFriends) returns "0.25"
    configSection.apply(api.DefaultConfigParams.QuestProbabilityFollowing) returns "0.45"
    configSection.apply(api.DefaultConfigParams.QuestProbabilityVIP) returns "0.10"

    configSection.apply(api.DefaultConfigParams.QuestProbabilityLevelsToGiveTutorialQuests) returns "4"
    configSection.apply(api.DefaultConfigParams.SolutionProbabilityLevelsToGiveStartingSolutions) returns "5"
    configSection.apply(api.DefaultConfigParams.SolutionProbabilityStartingVIPSolutions) returns "0.50"
    configSection.apply(api.DefaultConfigParams.SolutionProbabilityStartingFriendSolutions) returns "0.20"
    configSection.apply(api.DefaultConfigParams.SolutionProbabilityStartingFollowingSolutions) returns "0.20"

    configSection.apply(api.DefaultConfigParams.SolutionProbabilityFriends) returns "0.25"
    configSection.apply(api.DefaultConfigParams.SolutionProbabilityFollowing) returns "0.25"
    configSection.apply(api.DefaultConfigParams.SolutionProbabilityLiked) returns "0.20"
    configSection.apply(api.DefaultConfigParams.SolutionProbabilityVIP) returns "0.10"

    configSection.apply(api.DefaultConfigParams.BattleProbabilityLevelsToGiveStartingBattles) returns "5"
    configSection.apply(api.DefaultConfigParams.BattleProbabilityStartingVIPBattles) returns "0.50"
    configSection.apply(api.DefaultConfigParams.BattleProbabilityFriends) returns "0.25"
    configSection.apply(api.DefaultConfigParams.BattleProbabilityFollowing) returns "0.25"
    configSection.apply(api.DefaultConfigParams.BattleProbabilityLikedSolutions) returns "0.20"
    configSection.apply(api.DefaultConfigParams.BattleProbabilityVIP) returns "0.10"

    configSection.apply(api.DefaultConfigParams.QuestMaxDescriptionLength) returns "100"
    configSection.apply(api.DefaultConfigParams.SolutionMaxDescriptionLength) returns "100"
    configSection.apply(api.DefaultConfigParams.CommentsMaxLength) returns "420"

    configSection.apply(api.DefaultConfigParams.SolutionVoteTaskCountMean) returns "3"
    configSection.apply(api.DefaultConfigParams.SolutionVoteTaskCountDeviation) returns "1"
    configSection.apply(api.DefaultConfigParams.CreateSolutionTaskProbability) returns "0.5"
    configSection.apply(api.DefaultConfigParams.CreateVideoSolutionTaskProbability) returns "0.5"
    configSection.apply(api.DefaultConfigParams.AddToFollowingTaskProbability) returns "0.3"
    configSection.apply(api.DefaultConfigParams.QuestVoteTaskCountMean) returns "3"
    configSection.apply(api.DefaultConfigParams.QuestVoteTaskCountDeviation) returns "1"
    configSection.apply(api.DefaultConfigParams.CreateQuestTaskProbability) returns "0.3"
    configSection.apply(api.DefaultConfigParams.CreateVideoQuestTaskProbability) returns "0.3"
    configSection.apply(api.DefaultConfigParams.WriteCommentTaskProbability) returns "0.3"
    configSection.apply(api.DefaultConfigParams.ChallengeBattleTaskProbability) returns "0.3"
    configSection.apply(api.DefaultConfigParams.BattleVoteTaskProbability) returns "0.3"

    configSection.apply(api.DefaultConfigParams.FavoriteThemesShare) returns "0.20"
    configSection.apply(api.DefaultConfigParams.FavoriteThemesProbability) returns "0.75"

    configSection.apply(api.DefaultConfigParams.FavoriteThemesProbability) returns "0.75"
    configSection.apply(api.DefaultConfigParams.BattleCreationDelay) returns "24"

    configSection
  }

  private final def createStubTutorialConfig = {
    api.TutorialConfigParams returns _TutorialConfigParams

    val configSection = mock[ConfigSection]

    configSection
  }

  private final def applyConfigMock(): Unit = {
    api.config returns createStubDefaultConfig
    api.configNamed("Tutorial") returns createStubTutorialConfig
  }

  object context extends org.specs2.mutable.Before {
    def before = {
      applyConfigMock()

      api.db returns db
      db.challenge returns challenge

      api.user2Logic(any)
    }
  }
}

