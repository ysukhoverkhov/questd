package models.domain

import java.util.Date

/**
 * Result of daily activity for user.
 */
case class DailyResult(
  startOfPeriod: Date,
  dailyAssetsDecrease: Assets,
  decidedQuestSolutions: List[QuestSolutionResult] = List(),
  decidedQuestProposals: List[QuestProposalResult] = List(),
  questGiveUpAssetsDecrease: Option[Assets] = None,
  proposalGiveUpAssetsDecrease: Option[Assets] = None
  )

case class QuestSolutionResult(
    questSolutionId: String,
    reward: Option[Assets],
    penalty: Option[Assets],
    // TODO: make me not optional in 0.20. Make it not string.
    status: Option[String])
//    status: Option[QuestSolutionStatus.Value])
  
case class QuestProposalResult(
    questProposalId: String,
    reward: Option[Assets],
    penalty: Option[Assets],
    // TODO: make me not optional in 0.20. Make it not string.
    status: Option[String])
//    status: Option[QuestStatus.Value])
  
