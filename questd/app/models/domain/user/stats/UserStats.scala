package models.domain.user.stats

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
   * List of all solved quests with corresponding solution ids.
   */
  solvedQuests: Map[String, String] = Map.empty,

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
