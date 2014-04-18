package models.domain


/**
 * History of user dids. Outer list is a day, inner list is a did in a day.
 * We store here 2, 2 lists since salat serializes them incorrectly in other case.
 */
case class UserHistory(
  votedQuestProposalIds: List[List[String]] = List(List("", ""), List("", "")), // TODO: change me to 2 and fix all errors.
  likedQuestProposalIds: List[List[String]] = List(List("", ""), List("", "")), // TODO: add me on like.
  solvedQuestIds: List[List[String]] = List(List("", ""), List("", "")),
  votedQuestSolutionIds: List[List[String]] = List(List("", ""), List("", "")),
  
  selectedThemeIds: List[String] = List()
  )

// TODO: skip likedQuestProposals on day.