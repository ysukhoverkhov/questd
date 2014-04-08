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
   * How many quests we were reviewing during selection to take. 
   */
  questsReviewed: Int = 0,
  questsReviewedPast: Int = 0,
  
  /**
   * How many quests we've taken.
   */
  questsAccepted: Int = 0,
  questsAcceptedPast: Int = 0,

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

