package models.domain.battle

import java.util.Date

/**
 * Public info about battle.
 */
case class BattleInfo(
  status: BattleStatus.Value = BattleStatus.Fighting,
  solutionIds: List[String],
  authorIds: List[String],
  winnerIds: List[String] = List.empty,
  voteEndDate: Date
  )
