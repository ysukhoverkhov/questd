package logic

import java.util.Date

import controllers.domain.DomainAPIComponent
import models.domain._
import models.domain.battle.Battle
import org.joda.time.DateTime
import com.github.nscala_time.time.Imports._

class BattleLogic(
  val battle: Battle,
  val api: DomainAPIComponent#DomainAPI) {

  /**
   * We check is time come to stop voting for the battle.
   */
  def shouldStopVoting = {
    new Date().after(battle.info.voteEndDate)
  }

  /**
   * Time when to stop fighting battles.
   */
  def solutionVoteEndDate(questLevel: Int) = {
    BattleLogic.voteEndDate(questLevel)
  }

  /**
   * is the battle is resolved.
   * @return true if batle should be considered resolved.
   */
  def resolved = {
    shouldStopVoting
  }
}

object BattleLogic {

  def voteEndDate(questLevel: Int) = {
    val coef = questLevel match {
      case x if 1 to 10 contains x => 1
      case x if 11 to 16 contains x => 2
      case _ => 3
    }

    DateTime.now + coef.days toDate ()
  }

}

