package models.domain

/**
 * Statistics about user used to calculate how he affects shared things.
 */
case class UserStats (
  questsReviewed: Int = 0,
  questsAccepted: Int = 0,
  questsReviewedPast: Int = 0,
  questsAcceptedPast: Int = 0
  )

