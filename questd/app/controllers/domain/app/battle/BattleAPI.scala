package controllers.domain.app.battle

import components._
import controllers.domain._
import controllers.domain.app.user.RewardBattleParticipantsRequest
import controllers.domain.helpers._
import models.domain.battle.{Battle, BattleStatus}
import play.Logger

case class VoteBattleRequest(
  battle: Battle,
  solutionId: String,
  isFriend: Boolean)
case class VoteBattleResult()

case class UpdateBattleStateRequest(battle: Battle)
case class UpdateBattleStateResult()

private[domain] trait BattleAPI { this: DomainAPIComponent#DomainAPI with DBAccessor =>

  /**
   * Updates battle according to vote.
   */
  // TODO: test me.
  def voteBattle(request: VoteBattleRequest): ApiResult[VoteBattleResult] = handleDbException {
    import request._

    require(battle.info.status == BattleStatus.Fighting, "Only battles in Fighting state should be passed here")

    Logger.debug("API - voteBattle")

    {
      db.battle.updatePoints(
        id = battle.id,
        solutionId = solutionId,
        randomPointsChange = if (isFriend) 0 else 1,
        friendsPointsChange = if (isFriend) 1 else 0)
    } ifSome { b =>
      updateBattleState(UpdateBattleStateRequest(b)) map {
        OkApiResult(VoteBattleResult())
      }
    }
  }

  /**
   * Check update state of battle if we should.
   */
  def updateBattleState(request: UpdateBattleStateRequest): ApiResult[UpdateBattleStateResult] = handleDbException {
    import request._

    require(battle.info.status == BattleStatus.Fighting, "Only battles in Fighting state should be passed here")

    Logger.debug("API - updateBattleState")

    if (battle.shouldStopVoting) {
      val bestBattleSide = battle.info.battleSides.sortBy(battle.votingPoints)(Ordering[Int].reverse).head

      db.battle.updateStatus(
        id = battle.id,
        newStatus = BattleStatus.Resolved,
        setWinnerSolutionIds = battle.info.battleSides.filter(battle.votingPoints(_) == battle.votingPoints(bestBattleSide)).map(_.solutionId)) ifSome { b =>

        (if (b.info.status != battle.info.status) {
          rewardBattleParticipants(RewardBattleParticipantsRequest(b))
        } else {
          OkApiResult(UpdateBattleStateResult())
        }) map {
          OkApiResult(UpdateBattleStateResult())
        }
      }
    } else {
      OkApiResult(UpdateBattleStateResult())
    }
  }
}


