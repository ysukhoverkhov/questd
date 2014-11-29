package models.domain

import java.util.Date

/**
 * Public info about battle.
 */
case class BattleInfo(
  status: BattleStatus.Value = BattleStatus.Fighting,
  solutionIds: List[String],
  winnerId: Option[String] = None,
  voteEndDate: Date
  )

