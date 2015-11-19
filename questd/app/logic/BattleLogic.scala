package logic

import java.util.Date

import com.github.nscala_time.time.Imports._
import controllers.domain.{ApiResult, OkApiResult, DomainAPIComponent}
import controllers.domain.app.battle.{TuneBattlePointsBeforeResolveRequest, TuneBattlePointsBeforeResolveResult}
import models.domain.battle.{Battle, BattleSide}
import org.joda.time.DateTime

class BattleLogic(
  val battle: Battle,
  val api: DomainAPIComponent#DomainAPI) {

  /**
   * We check is time come to stop voting for the battle.
   */
  def shouldStopVoting = {
    val totalPoints = battle.info.battleSides.foldLeft(0) {
      case (total, side) => side.pointsRandom + side.pointsFriends + total
    }

    new Date().after(battle.info.voteEndDate) &&
      (totalPoints >= api.config(api.DefaultConfigParams.BattleMinVotesCount).toInt)
  }

  /**
   * Time when to stop fighting battles.
   */
  def solutionVoteEndDate(questLevel: Int) = {
    BattleLogic.voteEndDate(questLevel)
  }

  /**
   * Calculate points for battle.
   */
  def votingPoints(battleSide: BattleSide) = {
    List(
      battleSide.pointsRandom,
      battleSide.pointsFriends * constants.FriendsVoteMult).sum
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

