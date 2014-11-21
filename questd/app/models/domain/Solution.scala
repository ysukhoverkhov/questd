package models.domain

import models.domain.base.ID
import java.util.Date

object SolutionStatus extends Enumeration {
  val WaitingForCompetitor, OnVoting, Won, Lost, CheatingBanned, IACBanned = Value
}

case class Solution(
  id: String = ID.generateUUID(),
  cultureId: String,
  lastModDate: Date = new Date(),
  rivalSolutionId: Option[String] = None,
  questLevel: Int,
  info: SolutionInfo,
  rating: SolutionRating = SolutionRating(),
  voteEndDate: Date,
  status: SolutionStatus.Value = SolutionStatus.OnVoting) extends ID
