package models.domain.battle

import java.util.Date

/**
 * Public info about battle.
 */
case class BattleInfo(
  status: BattleStatus.Value = BattleStatus.Fighting,
  battleSides: List[BattleSide],
  voteEndDate: Date
  )
