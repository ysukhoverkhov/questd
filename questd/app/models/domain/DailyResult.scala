package models.domain

import java.util.Date

/**
 * Result of daily activity for user.
 */
case class DailyResult(
  startOfPeriod: Date,
  decidedSolutions: List[SolutionResult] = List.empty,
  decidedQuests: List[QuestResult] = List.empty,
  questsIncome: List[QuestIncome] = List.empty)

case class SolutionResult(
    solutionId: String,
    battleId: Option[String] = None,
    reward: Option[Assets],
    penalty: Option[Assets],
    status: SolutionStatus.Value)

case class QuestResult(
    questId: String,
    reward: Option[Assets],
    penalty: Option[Assets],
    status: QuestStatus.Value)

/**
 * Income for a single quest per day.
 * @param questId Id of a quest income calculated for.
 * @param passiveIncome Passive income we receive just because we have a quest.
 * @param timesLiked Number of times our quest was liked since creation.
 * @param likesIncome Income for quest likes.
 * @param timesSolved Number of times our quest was solved today.
 * @param solutionsIncome Income for solving our quest.
 */
case class QuestIncome(
  questId: String,
  passiveIncome: Assets,
  timesLiked: Int,
  likesIncome: Assets,
  timesSolved: Int = 0,
  solutionsIncome: Assets = Assets())
