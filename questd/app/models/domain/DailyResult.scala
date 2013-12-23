package models.domain

import java.util.Date

/**
 * Result of daily activity for user.
 */
case class DailyResult(
  startOfPeriod: Date,
  dailyAssetsDecrease: Assets,
  decidedQuestSolutions: List[QuestSolutionResult] = List(),
  decidedQuestProposals: List[QuestProposalResult] = List()
  )
  
  
case class QuestSolutionResult(
    questSolutionId: String,
    reward: Option[Assets],
    penalty: Option[Assets])
  
case class QuestProposalResult(
    questProposalId: String,
    reward: Option[Assets],
    penalty: Option[Assets])
  
