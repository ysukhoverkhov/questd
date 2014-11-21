package models.domain

import java.util.Date

import models.domain.base.ID

object BattleStatus extends Enumeration {
  val Fighting, Resolved = Value
}

/**
 * A battle our solution participated to.
 */
case class Battle(
  id: String = ID.generateUUID(),
  status: BattleStatus.Value = BattleStatus.Fighting,
  solutionIds: List[String],
  winnerId: Option[String] = None,
  voteEndDate: Date
  ) extends ID

