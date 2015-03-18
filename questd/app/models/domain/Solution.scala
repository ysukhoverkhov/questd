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
  battleIds: List[String] = List.empty,
  questLevel: Int,
  info: SolutionInfo,
  rating: SolutionRating = SolutionRating(),
  status: SolutionStatus.Value = SolutionStatus.WaitingForCompetitor) extends ID
