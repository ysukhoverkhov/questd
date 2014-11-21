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
  battles: List[Battle] = List(),
  questLevel: Int, // TODO: check remove me.
  info: SolutionInfo,
  rating: SolutionRating = SolutionRating(),
  status: SolutionStatus.Value = SolutionStatus.WaitingForCompetitor) extends ID
