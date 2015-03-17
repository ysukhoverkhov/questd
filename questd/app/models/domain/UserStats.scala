package models.domain

import java.util.Date

/**
 * Statistics about user used to calculate how he affects shared things.
 */
case class UserStats (

  /**
   * When statistics was shifted last.
   */
  lastStatShift: Date = new Date(0),

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

