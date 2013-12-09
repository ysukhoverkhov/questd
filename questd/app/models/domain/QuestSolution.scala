package models.domain

import models.domain.base.ID

object QuestSolutionStatus extends Enumeration {
  val OnVoting, Voted, CheatingBanned, IACBanned = Value
}

case class QuestSolution(
  id: String = ID.generateUUID(),
  questID: String,
  userID: String,
  info: QuestSolutionInfo,
  rating: QuestSolutionRating = QuestSolutionRating(),
  status: String = QuestSolutionStatus.OnVoting.toString) extends ID
