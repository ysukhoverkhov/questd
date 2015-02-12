package models.domain

import java.util.Date

object BattleStatus extends Enumeration {
  val Fighting, Resolved = Value
}

/**
 * Public info about battle.
 */
case class BattleInfo(
  status: BattleStatus.Value = BattleStatus.Fighting,
  solutionIds: List[String],
  authorIds: List[String],
  winnerId: Option[String] = None,
  voteEndDate: Date
  )

