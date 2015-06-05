package models.domain.user

import models.domain.base.ID
import models.domain.common.Assets
import models.domain.quest.QuestSolutionContext
import models.domain.tutorial.TutorialPlatform
import models.domain.user.dailyresults.DailyResult
import models.domain.user.message.Message

/**
 * This can be given to client as is, thus contains only public information.
 */
case class Profile(
  profileId: String = ID.generateUUID(),
  profileVersion: Int = 1,
  publicProfile: PublicProfile = PublicProfile(),
  ratingToNextLevel: Int = 0,
  assets: Assets = Assets(100000, 0, 0), // Should be (0, 0, 0) here.
  rights: Rights = Rights(),
  dailyResults: List[DailyResult] = List.empty,
  dailyTasks: DailyTasks = DailyTasks(),
  messages: List[Message] = List.empty,
  questCreationContext: QuestCreationContext = QuestCreationContext(),
  questSolutionContext: QuestSolutionContext = QuestSolutionContext(),
  questVoteContext: QuestVoteContext = QuestVoteContext(),
  solutionVoteContext: SolutionVoteContext = SolutionVoteContext(),
  tutorialStates: Map[String, TutorialState] =
    TutorialPlatform.values.foldLeft[Map[String, TutorialState]](Map.empty){(r, v) => r + (v.toString -> TutorialState())},
  analytics: Analytics = Analytics(),
  debug: String = "")
