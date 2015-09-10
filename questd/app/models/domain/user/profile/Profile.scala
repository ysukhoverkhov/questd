package models.domain.user.profile

import models.domain.base.ID
import models.domain.common.{ClientPlatform, Assets}
import models.domain.quest.QuestSolutionContext
import models.domain.user.dailyresults.DailyResult
import models.domain.user.message.Message

/**
 * This can be given to client as is, thus contains only public information.
 */
case class Profile(
  profileId: String = ID.generate,
  profileVersion: Int = 1,
  publicProfile: PublicProfile = PublicProfile(),
  ratingToNextLevel: Int = 0,
  assets: Assets = Assets(0, 0, 0),
  rights: Rights = Rights(),
  dailyResults: List[DailyResult] = List.empty,
  dailyTasks: DailyTasks = DailyTasks(),
  messages: List[Message] = List.empty,
  questCreationContext: QuestCreationContext = QuestCreationContext(),
  questSolutionContext: QuestSolutionContext = QuestSolutionContext(),
  questVoteContext: QuestVoteContext = QuestVoteContext(),
  solutionVoteContext: SolutionVoteContext = SolutionVoteContext(),
  tutorialStates: Map[String, TutorialState] =
    ClientPlatform.values.foldLeft[Map[String, TutorialState]](Map.empty){(r, v) => r + (v.toString -> TutorialState())},
  analytics: Analytics = Analytics(),
  debug: String = "")
