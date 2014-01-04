package models.domain

/**
 * History of user dids.
 */
case class UserHistory(
  votedQuestProposalIds: List[List[String]] = List(),
  votedQuestSolutionIds: List[List[String]] = List())

