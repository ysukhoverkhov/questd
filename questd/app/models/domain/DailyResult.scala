package models.domain

import java.util.Date

/**
 * Result of daily activity for user.
 */
case class DailyResult(
  startOfPeriod: Date,
  dailySalary: Assets,
  decidedQuestSolutions: List[QuestSolutionResult] = List(),
  decidedQuestProposals: List[QuestProposalResult] = List())

case class QuestSolutionResult(
    questSolutionId: String,
    reward: Option[Assets],
    penalty: Option[Assets],
    status: QuestSolutionStatus.Value)

case class QuestProposalResult(
    questProposalId: String,
    reward: Option[Assets],
    penalty: Option[Assets],
    status: QuestStatus.Value)

