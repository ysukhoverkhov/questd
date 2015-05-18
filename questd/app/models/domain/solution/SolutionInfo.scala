package models.domain.solution

/**
 * Public info about solution.
 */
case class SolutionInfo(
  content: SolutionInfoContent,
  vip: Boolean,
  authorId: String,
  questId: String)
