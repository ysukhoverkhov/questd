package models.domain


/**
 * Rating of a quest used during voting.
 */
case class SolutionRating(
  reviewsCount: Int = 0,
  pointsRandom: Int = 0,
  pointsFriends: Int = 0,
  likesCount: Int = 0,
  cheating: Int = 0,
  iacpoints: IAContentRating = IAContentRating())
