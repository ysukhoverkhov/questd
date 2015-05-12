package models.domain.user

import java.util.Date

/**
 * Result of daily activity for user.
 */
case class DailyResult(
  startOfPeriod: Date,
  decidedSolutions: List[SolutionResult] = List.empty,
  decidedQuests: List[QuestResult] = List.empty,
  questsIncome: List[QuestIncome] = List.empty)
