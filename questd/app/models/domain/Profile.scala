package models.domain


/**
 * This can be given to client as is, thus contains only public information.
 */
case class Profile(
  level: Int = 20,
  ratingToNextLevel: Int = 100000,
  bio: Bio = Bio(),
  assets: Assets = Assets(100000, 1000, 0),
  rights: Rights = Rights(),
  dailyResults: List[DailyResult] = List(),
  questProposalContext: QuestProposalConext = QuestProposalConext(),
  questSolutionContext: QuestSolutionContext = QuestSolutionContext(),
  questProposalVoteContext: QuestProposalVoteContext = QuestProposalVoteContext(),
  questSolutionVoteContext: QuestSolutionVoteContext = QuestSolutionVoteContext())

