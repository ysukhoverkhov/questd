package models.domain.battle

import java.util.Date

import models.domain.common.Assets

/**
 * Public info about battle.
 */
case class BattleInfo(
  status: BattleStatus.Value = BattleStatus.Fighting,
  battleSides: List[BattleSide],
  voteEndDate: Date,
  victoryReward: Assets,
  defeatReward: Assets
  )
