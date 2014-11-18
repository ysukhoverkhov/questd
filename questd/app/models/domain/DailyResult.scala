package models.domain

import java.util.Date

/**
 * Result of daily activity for user.
 */
case class DailyResult(
  startOfPeriod: Date,
  dailySalary: Assets,
  decidedQuestSolutions: List[QuestSolutionResult] = List(),
  decidedQuestProposals: List[QuestProposalResult] = List(),
  questsIncome: List[QuestsIncome])

case class QuestSolutionResult(
    solutionId: String,
    reward: Option[Assets],
    penalty: Option[Assets],
    status: QuestSolutionStatus.Value)

case class QuestProposalResult(
    questId: String,
    reward: Option[Assets],
    penalty: Option[Assets],
    status: QuestStatus.Value)

/**
 * Income for a single quest per day.
 * @param questId Id of a quest income calculated for.
 * @param passiveIncome Passive income we receive just because we have a quest.
 * @param timesLiked Number of times our quest was liked today.
 * @param likesIncome Income for liking of our quest.
 * @param timesSolved Number of times our quest was solved today.
 * @param solutionsIncome Income for solving our quest.
 */
case class QuestsIncome(
  questId: String,
  passiveIncome: Assets,
  timesLiked: Int = 0,
  likesIncome: Assets = Assets(),
  timesSolved: Int = 0,
  solutionsIncome: Assets = Assets())
