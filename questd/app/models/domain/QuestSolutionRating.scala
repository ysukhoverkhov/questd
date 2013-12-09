package models.domain

object QuestSolutionVote extends Enumeration {
  val Cool, SoSo, Cheating, IASpam, IAPorn = Value
}

/**
 * Rating of a quest used during voting.
 */
case class QuestSolutionRating(
  pointsRandom: Int = 0,
  pointsFriends: Int = 0,
  pointsInvited: Int = 0,
  cheating: Int = 0,
  iacpoints: IAContentRating = IAContentRating())
