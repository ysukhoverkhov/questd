package models.domain.solution

import java.util.Date

import models.domain.base.ID

/**
 * Solution itself.
 */
case class Solution(
  id: String = ID.generateUUID(),
  cultureId: String,
  lastModDate: Date = new Date(),
  battleIds: List[String] = List.empty,
  questLevel: Int,
  info: SolutionInfo,
  rating: SolutionRating = SolutionRating(),
  status: SolutionStatus.Value = SolutionStatus.WaitingForCompetitor) extends ID
