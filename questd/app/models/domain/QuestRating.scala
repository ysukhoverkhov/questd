package models.domain

object QuestProposalVote extends Enumeration {
  val Cool, SoSo, Cheating, IASpam, IAPorn = Value
}

/**
 * Rating of a quest used during voting.
 */
case class QuestRating(
    // The filed is by group.
  points: Int = 0,
  cheating: Int = 0,
  iacpoints: IAContentRating = IAContentRating(),
  votersCount: Int = 0)
