package models.domain.solution

import java.util.Date

/**
 * Public info about solution.
 */
case class SolutionInfo(
  content: SolutionInfoContent,
  vip: Boolean,
  authorId: String,
  questId: String,
  creationDate: Date = new Date()
  )
