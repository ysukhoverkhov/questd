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

        Logger.error(s"!!!!! other - ${other.info.authorId}")
        Logger.error(s"!!!!! request - ${request.solution.info.authorId}")


        if (other.info.authorId != request.solution.info.authorId) {
          Logger.error(s"???????")

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
        Logger.error(s"1111111")
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


  /**
   * Tries to find competitor to us on quest and resolve our battle. Updates db after that.
   */
  // TODO: clean me up.
//  def tryFightQuest(request: TryFightQuestRequest): ApiResult[TryFightQuestResult] = handleDbException {
//    // 1. find all solutions with the same quest id with status waiting for competitor.
//
//    val possibleCompetitors = db.solution.allWithParams(
//      status = List(SolutionStatus.WaitingForCompetitor),
//      questIds = List(request.solution.info.questId))
//
//    def fight(s1: Solution, s2: Solution): (List[Solution], List[Solution]) = {
//      if (s1.calculatePoints == s2.calculatePoints)
//        (List(s1, s2), List())
//      else if (s1.calculatePoints > s2.calculatePoints)
//        (List(s1), List(s2))
//      else
//        (List(s2), List(s1))
//    }
//
//    @tailrec
//    def compete(solutions: Iterator[Solution]): ApiResult[TryFightQuestResult] = {
//      if (solutions.hasNext) {
//        val other = solutions.next()
//
//        if (other.info.authorId != request.solution.info.authorId) {
//
//          Logger.debug("Found fight pair for quest " + request.solution + ":")
//          Logger.debug("  s1.id=" + request.solution.id)
//          Logger.debug("  s2.id=" + other.id)
//
//          // Updating solution rivals
//          val ourSol = request.solution.copy(rivalSolutionId = Some(other.id))
//          val otherSol = other.copy(rivalSolutionId = Some(request.solution.id))
//
//          // Compare two solutions.
//          val (winners, losers) = fight(otherSol, ourSol)
//
//          // update solutions, winners
//          for (curSol <- winners) {
//            Logger.debug("  winner id=" + curSol.id)
//
//            db.solution.updateStatus(curSol.id, SolutionStatus.Won, curSol.rivalSolutionId) ifSome { s =>
//              db.user.readById(curSol.info.authorId) ifSome { u =>
//                rewardSolutionAuthor(RewardSolutionAuthorRequest(solution = s, author = u))
//              }
//            }
//
//          }
//
//          // and losers
//          for (curSol <- losers) {
//            Logger.debug("  loser id=" + curSol.id)
//
//            db.solution.updateStatus(curSol.id, SolutionStatus.Lost, curSol.rivalSolutionId) ifSome { s =>
//              db.user.readById(curSol.info authorId) ifSome { u =>
//                rewardSolutionAuthor(RewardSolutionAuthorRequest(solution = s, author = u))
//              }
//            }
//
//          }
//
//          OkApiResult(TryFightQuestResult())
//
//        } else {
//
//          // Skipping to next if current is we are.
//          compete(solutions)
//        }
//      } else {
//
//        // We didn;t find competitor but this is ok.
//        OkApiResult(TryFightQuestResult())
//      }
//    }
//
//    compete(possibleCompetitors)
//  }

}

