package models.domain.user

import models.domain.common.ContentVote

/**
 * Solutions in battle we participated in.
 */
case class SolutionsInBattle(
  solutionIds: List[String] = List.empty
  )

/**
 * Statistics about user used to calculate how he affects shared things.
 */
case class UserStats (

  /**
   * List of all our created quests.
   */
  createdQuests: List[String] = List.empty,

  /**
   * All voted quests.
   */
  votedQuests: Map[String, ContentVote.Value] = Map.empty,

  /**
   * List of all solved quests.
   */
  solvedQuests: List[String] = List.empty,

  /**
   * List of all our created solutions.
   */
  createdSolutions: List[String] = List.empty,

  /**
   * All voted solutions.
   */
  votedSolutions: Map[String, ContentVote.Value] = Map.empty,

  /**
   * Ids of battles we participated in. Key is competitor solution id.
   */
  participatedBattles: Map[String, SolutionsInBattle] = Map.empty,

  /**
   * All voted battles. Value is id of solution in battle we voted for.
   */
  votedBattles: Map[String, String] = Map.empty
  )
