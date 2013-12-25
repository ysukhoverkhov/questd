package models.domain


/**
 * This can be given to client as is, thus contains only public information.
 */
case class Profile(
  level: Int = 1,
  ratingToNextLevel: Int = 1000,
  bio: Bio = Bio(),
  assets: Assets = Assets(0, 0, 0),
  rights: Rights = Rights(),
  dailyResults: List[DailyResult] = List(),
  questProposalContext: QuestProposalConext = QuestProposalConext(),
  questSolutionContext: QuestSolutionContext = QuestSolutionContext(),
  questProposalVoteContext: QuestProposalVoteContext = QuestProposalVoteContext(),
  questSolutionVoteContext: QuestSolutionVoteContext = QuestSolutionVoteContext())

