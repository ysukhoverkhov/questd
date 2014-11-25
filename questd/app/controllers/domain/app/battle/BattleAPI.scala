package controllers.domain.app.battle

import models.domain._
import components._
import controllers.domain._
import controllers.domain.helpers._
import controllers.domain.app.user._
import play.Logger

case class UpdateBattleStateRequest(battle: Battle)
case class UpdateBattleStateResult()

private[domain] trait BattleAPI { this: DomainAPIComponent#DomainAPI with DBAccessor =>

  /**
   * Update state of quest solution with votes.
   */
  def updateBattleState(request: UpdateBattleStateRequest): ApiResult[UpdateBattleStateResult] = handleDbException {
    import request._

    Logger.debug("API - updateBattleState")

    def checkResolved(b: Battle): Option[Battle] = {
      if (b.resolved) {
        val solutions: List[Solution] = b.solutionIds.map { s =>
          db.solution.readById(s)
        }.flatten

        val bestSolution = solutions.sortBy(_.votingPoints).head

        solutions.foreach { s =>
          val status = if (s.votingPoints == bestSolution.votingPoints)
            SolutionStatus.Won
          else
            SolutionStatus.Lost
          db.solution.updateStatus(s.id, status)
        }

        db.battle.updateStatus(b.id, BattleStatus.Resolved)
      } else
        Some(b)
    }

    val functions = List(
      checkResolved _)

    val updatedBattle = functions.foldLeft[Option[Battle]](Some(battle))((r, f) => {
      r.flatMap(f)
    })

    updatedBattle ifSome { b =>

      val authorsUpdateResult: OkApiResult[UpdateBattleStateResult] =
        if (b.status != battle.status) {

          val solutions: List[Solution] = b.solutionIds.map { s =>
            db.solution.readById(s)
          }.flatten

          solutions.foreach { s =>
            val authorId = s.info.authorId

            db.user.readById(authorId) match {
              case None =>
                Logger.error("Unable to find author of quest solution user " + authorId)
                InternalErrorApiResult()
              case Some(author) =>
                rewardSolutionAuthor(RewardSolutionAuthorRequest(s, author))
            }
          }

          OkApiResult(UpdateBattleStateResult())
        } else {
          OkApiResult(UpdateBattleStateResult())
        }

      authorsUpdateResult ifOk OkApiResult(UpdateBattleStateResult())
    }
  }
}


