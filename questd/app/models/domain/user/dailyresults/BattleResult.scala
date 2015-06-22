package models.domain.user.dailyresults

import models.domain.common.Assets

/**
 * Result of solution creation.
 */
case class BattleResult(
  battleId: String,
  reward: Assets,
  isVictory: Boolean)
