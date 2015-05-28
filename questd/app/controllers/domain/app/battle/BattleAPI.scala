package controllers.domain.app.battle

import components._
import controllers.domain._
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
   * Update state of quest solution with votes.
   */
  // TODO: implement me.
  // TODO: test me.
  def updateBattleState(request: UpdateBattleStateRequest): ApiResult[UpdateBattleStateResult] = handleDbException {
    import request._

    require(battle.info.status == BattleStatus.Fighting, "Only battles in Fighting state should be passed here")

    Logger.debug("API - updateBattleState")

//    def solutionsOfBattle(b: Battle): List[Solution] = {
//      b.info.battleSides.flatMap { s =>
//        db.solution.readById(s.solutionId)
//      }
//    }
//
//    def checkResolved(b: Battle): Option[Battle] = {
//      if (b.resolved) {
//        val solutions = solutionsOfBattle(b)
//
//        val bestSolution = solutions.sortBy(_.votingPoints)(Ordering[Int].reverse).head
//
//        solutions.foreach { s =>
//          val status = if (s.votingPoints == bestSolution.votingPoints)
//            SolutionStatus.Won
//          else
//            SolutionStatus.Lost
//          db.solution.updateStatus(s.id, status)
//        }
//
//        db.battle.updateStatus(b.id, BattleStatus.Resolved, solutions.filter(_.votingPoints == bestSolution.votingPoints).map(_.info.authorId))
//      } else
//        Some(b)
//    }
//
//    val functions = List(
//      checkResolved _)
//
//    val updatedBattle = functions.foldLeft[Option[Battle]](Some(battle))((r, f) => {
//      r.flatMap(f)
//    })
//
//    updatedBattle ifSome { b =>
//
//      val authorsUpdateResult: OkApiResult[UpdateBattleStateResult] =
//        if (b.info.status != battle.info.status) {
//
//          val solutions = solutionsOfBattle(b)
//
//          solutions.foreach { s =>
//            val authorId = s.info.authorId
//
//            db.user.readById(authorId) ifSome { author =>
//              rewardSolutionAuthor(RewardSolutionAuthorRequest(solution = s, author = author, battle = Some(b)))
//            }
//          }
//
//          OkApiResult(UpdateBattleStateResult())
//        } else {
//          OkApiResult(UpdateBattleStateResult())
//        }
//
//      authorsUpdateResult map OkApiResult(UpdateBattleStateResult())
//    }
    OkApiResult(UpdateBattleStateResult())
  }
}


