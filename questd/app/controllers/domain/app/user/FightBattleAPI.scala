package controllers.domain.app.user

import logic.BattleLogic
import scala.language.postfixOps
import models.domain._
import play.Logger
import controllers.domain.helpers._
import controllers.domain._
import components._


case class TryCreateBattleRequest(solution: Solution)
case class TryCreateBattleResult()


private[domain] trait FightBattleAPI { this: DomainAPIComponent#DomainAPI with DBAccessor =>

  /**
   * Tries to match solution with competitor, leaves it as it is if not found.
   * @param request Request with solution to find competitor for.
   * @return Result of competitor search.
   */
  def tryCreateBattle(request: TryCreateBattleRequest): ApiResult[TryCreateBattleResult] = handleDbException {
    import request._

    def selectCompetitor(possibleCompetitors: Iterator[Solution]): Option[Solution] = {
      if (possibleCompetitors.hasNext) {
        val other = possibleCompetitors.next()

        if (other.info.authorId != request.solution.info.authorId) {

          Logger.debug("Found fight pair for quest " + request.solution.info.questId + " :")
          Logger.debug("  s1.id=" + request.solution.id)
          Logger.debug("  s2.id=" + other.id)

          Some(other)

        } else {
          // Skipping to next if current is we are.
          selectCompetitor(possibleCompetitors)
        }
      } else {
        None
      }
    }

    val possibleCompetitors = db.solution.allWithParams(
      status = List(SolutionStatus.WaitingForCompetitor),
      questIds = List(solution.info.questId),
      cultureId = Some(solution.cultureId))

    selectCompetitor(possibleCompetitors) match {
      case Some(competitor) =>
        // FIX: transaction should be here as this operation is atomic.
        val battle = Battle(
          solutionIds = List(solution.id, competitor.id),
          voteEndDate = BattleLogic.voteEndDate(solution.questLevel)
        )
        db.battle.create(battle)

        battle.solutionIds.foreach {
          db.solution.updateStatus(_, SolutionStatus.OnVoting, Some(battle.id))
        }

        OkApiResult(TryCreateBattleResult())

      case None =>
        OkApiResult(TryCreateBattleResult())
    }
  }

}

