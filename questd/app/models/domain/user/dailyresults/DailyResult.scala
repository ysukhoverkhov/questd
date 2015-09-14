package models.domain.user.dailyresults

import java.util.Date

/**
 * Result of daily activity for user.
 */
case class DailyResult(
  startOfPeriod: Date,
  decidedQuests: List[QuestResult] = List.empty,
  decidedSolutions: List[SolutionResult] = List.empty,
  decidedBattles: List[BattleResult] = List.empty,
  questsIncome: List[QuestIncome] = List.empty)
