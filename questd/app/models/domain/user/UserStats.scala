package models.domain.user

import models.domain.common.ContentVote

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

  // TODO: test it's filled
  // TODO: test it's used for voting.
  /**
   * Battles we took part in.
   */
  participatedBattles: List[String] = List.empty,

  // TODO: test it's filled
  // TODO: test it's used for voting.
  /**
   * All voted battles. Value is id of solution in battle we voted for.
   */
  votedBattles: Map[String, String] = Map.empty
  )
