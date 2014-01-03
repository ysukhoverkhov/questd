package models.domain

import models.domain.base.ID
import java.util.Date

object QuestSolutionStatus extends Enumeration {
  val OnVoting, WaitingForCompetitor, Won, Lost, CheatingBanned, IACBanned = Value
}

case class QuestSolution(
  id: String = ID.generateUUID(),
  lastModDate: Date = new Date(),
  questID: String,
  userID: String,
  rivalSolutionId: Option[String] = None,
  questLevel: Int,
  info: QuestSolutionInfo,
  rating: QuestSolutionRating = QuestSolutionRating(),
  status: String = QuestSolutionStatus.OnVoting.toString) extends ID
