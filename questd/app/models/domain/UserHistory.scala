package models.domain

/**
 * History of user dids.
 */
case class UserHistory(
  votedQuestProposalIds: List[List[String]] = List(List("", ""), List("", "")),
  solvedQuestIds: List[List[String]] = List(List("", ""), List("", "")),
  votedQuestSolutionIds: List[List[String]] = List(List("", ""), List("", "")))

// We store here 2, 2 lists since salat serializes them incorrectly in other case.