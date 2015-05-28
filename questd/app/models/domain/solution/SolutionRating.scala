package models.domain.solution

import models.domain.common.IAContentRating

/**
 * Rating of a quest used during voting.
 */
case class SolutionRating(
  votersCount: Int = 0,
  timelinePoints: Int = 0,
  likesCount: Int = 0,
  cheating: Int = 0,
  iacpoints: IAContentRating = IAContentRating())

