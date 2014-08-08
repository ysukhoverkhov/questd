package models.domain

import models.domain.base.ID

/**
 * This can be given to client as is, thus contains only public information.
 */
case class Profile(
  profileId: String = ID.generateUUID(),
  profileVersion: Int = 1,
  publicProfile: PublicProfile = PublicProfile(),
  ratingToNextLevel: Int = 0,
  assets: Assets = Assets(100000, 1000, 0), // Should be (0, 0, 0) here.
  rights: Rights = Rights(),
  dailyResults: List[DailyResult] = List(),
  dailyTasks: DailyTasks = DailyTasks(),
  questProposalContext: QuestProposalConext = QuestProposalConext(),
  questSolutionContext: QuestSolutionContext = QuestSolutionContext(),
  questProposalVoteContext: QuestProposalVoteContext = QuestProposalVoteContext(),
  questSolutionVoteContext: QuestSolutionVoteContext = QuestSolutionVoteContext(),
  analytics: Analytics = Analytics(),
  debug: String = "")

