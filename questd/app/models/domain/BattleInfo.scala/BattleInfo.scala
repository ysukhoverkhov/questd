package models.domain

import java.util.Date

import models.domain.base.ID

/**
 * Public info about battle.
 */
case class BattleInfo(
  id: String = ID.generateUUID(),
  status: BattleStatus.Value = BattleStatus.Fighting,
  solutionIds: List[String],
  winnerId: Option[String] = None,
  voteEndDate: Date
  ) extends ID

