package models.domain

/**
 * Statistics about user used to calculate how he affects shared things.
 */
case class UserStats (

  /**
   * List of all our created quests.
   */
  createdQuests: List[String] = List.empty,

  /**
   * List of all our created solutions.
   */
  createdSolutions: List[String] = List.empty,

  /**
   * List of all solved quests.
   */
  solvedQuests: List[String] = List.empty,

  /**
   * All voted quests.
   */
  votedQuests: Map[String, ContentVote.Value] = Map.empty,

  /**
   * All voted solutions.
   */
  votedSolutions: Map[String, ContentVote.Value] = Map.empty
  )

