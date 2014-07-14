package models.domain

import models.domain.base.ID
import java.util.Date

object QuestSolutionStatus extends Enumeration {
  val OnVoting, WaitingForCompetitor, Won, Lost, CheatingBanned, IACBanned = Value
}

case class QuestSolution(
  id: String = ID.generateUUID(),
  lastModDate: Date = new Date(),
  userId: String,
  rivalSolutionId: Option[String] = None,
  questLevel: Int,
  info: QuestSolutionInfo,
  rating: QuestSolutionRating = QuestSolutionRating(),
  // TODO: remove new Date() in version 0.20.03
  voteEndDate: Date = new Date(),
  status: String = QuestSolutionStatus.OnVoting.toString) extends ID
