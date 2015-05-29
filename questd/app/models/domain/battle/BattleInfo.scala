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

  // TODO: calculate reward for wining battle at the start of the battle and store it in battle.
  victoryReward: Assets = Assets(),
  defeatReward: Assets = Assets()

  )
