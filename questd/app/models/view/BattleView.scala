package models.view

import models.domain.battle.{Battle, BattleInfo}
import models.domain.user.User

case class BattleView (
  id: String,
  info: BattleInfo,
  myVotedSolutionId: Option[String])

/**
 * Companion.
 */
object BattleView {
  def apply(b: Battle, u: User): BattleView = {
    BattleView(
      id = b.id,
      info = b.info,
      myVotedSolutionId = u.stats.votedBattles.get(b.id))
  }
}
