package models.domain

import models.domain.base.ID
import java.util.Date

object QuestSolutionStatus extends Enumeration {
  val OnVoting, WaitingForCompetitor, Won, Lost, CheatingBanned, IACBanned = Value
}

case class QuestSolution(
  id: String = ID.generateUUID(),
  cultureId: String,
  lastModDate: Date = new Date(),
  rivalSolutionId: Option[String] = None,
  questLevel: Int,
  info: QuestSolutionInfo,
  rating: QuestSolutionRating = QuestSolutionRating(),
  // TODO: remove = new Date() in 0.30
  voteEndDate: Date = new Date(),
  status: QuestSolutionStatus.Value = QuestSolutionStatus.OnVoting) extends ID
