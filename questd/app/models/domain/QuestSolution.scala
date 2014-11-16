package models.domain

import models.domain.base.ID
import java.util.Date

object QuestSolutionStatus extends Enumeration {
  val WaitingForCompetitor, OnVoting, Won, Lost, CheatingBanned, IACBanned = Value
}

case class QuestSolution(
  id: String = ID.generateUUID(),
  cultureId: String,
  lastModDate: Date = new Date(),
  rivalSolutionId: Option[String] = None,
  questLevel: Int,
  info: QuestSolutionInfo,
  rating: QuestSolutionRating = QuestSolutionRating(),
  voteEndDate: Date,
  status: QuestSolutionStatus.Value = QuestSolutionStatus.OnVoting) extends ID
