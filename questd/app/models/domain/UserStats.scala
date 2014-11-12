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
  solvedQuests: List[String] = List(),

// TODO: check remove all 4 bellow.
  /**
   * How many proposals reviewed since last stats update during voting.
   */
  proposalsVoted: Int = 0,
  /**
   * How many proposals per day reviewed.
   */
  proposalsVotedPerDay: Double = 0,

  /**
   * How many reviewed proposals voted up since last stats update.
   */
  proposalsLiked: Int = 0,
  /**
   * How many reviewed proposals accepted per day.
   */
  proposalsLikedPerDay: Double = 0
  )

