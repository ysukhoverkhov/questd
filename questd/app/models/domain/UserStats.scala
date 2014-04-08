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
   * How many quests we were reviewing during voting. 
   */
  questsReviewed: Int = 0,
  questsReviewedPast: Int = 0,
  
  /**
   * How many quests we've liked.
   */
  questsAccepted: Int = 0,
  questsAcceptedPast: Int = 0,

  /**
   * How many proposals reviewed since last stats update.
   */
  proposalsReviewed: Int = 0,
  /**
   * How many proposals per day reviewed.
   */
  proposalsReviewedPerDay: Double = 0,
  
  /**
   * How many reviewed proposals accepted since last stats update.
   */
  proposalsAccepted: Int = 0,
  /**
   * How many reviewed proposals accepted per day.
   */
  proposalsAcceptedPerDay: Double = 0
  )

