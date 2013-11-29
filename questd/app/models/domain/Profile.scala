package models.domain


/**
 * This can be given to client as is, thus contains only public information.
 */
case class Profile(
  level: Int = 1,
  bio: Bio = Bio(),
  assets: Assets = Assets(0, 0, 0),
  rights: Rights = Rights(),
  questProposalContext: QuestProposalConext = QuestProposalConext(),
  questSolutionContext: QuestSolutionContext = QuestSolutionContext(),
  questProposalVoteContext: QuestProposalVoteConext = QuestProposalVoteConext())

