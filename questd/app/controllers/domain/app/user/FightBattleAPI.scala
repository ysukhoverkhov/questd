package controllers.domain.app.user

import components._
import controllers.domain._
import controllers.domain.helpers._
import logic.BattleLogic
import models.domain.battle.{Battle, BattleInfo, BattleSide, BattleStatus}
import models.domain.solution.{Solution, SolutionStatus}
import models.domain.user.{SolutionsInBattle, TimeLineReason, TimeLineType}
import play.Logger

import scala.language.postfixOps


case class TryCreateBattleRequest(solution: Solution)
case class TryCreateBattleResult()

case class RewardBattleParticipantsRequest(battle: Battle)
case class RewardBattleParticipantsResult()

private[domain] trait FightBattleAPI { this: DomainAPIComponent#DomainAPI with DBAccessor =>

  /**
   * Tries to match solution with competitor, leaves it as it is if not found.
   * @param request Request with solution to find competitor for.
   * @return Result of competitor search.
   */
  def tryCreateBattle(request: TryCreateBattleRequest): ApiResult[TryCreateBattleResult] = handleDbException {
    import request._

    Logger.trace(s"Trying to create battle")

    def selectCompetitor(possibleCompetitors: Iterator[Solution]): Option[Solution] = {
      if (possibleCompetitors.hasNext) {
        val other = possibleCompetitors.next()

        Logger.trace(s"    Analysing competitor solution ${other.id} - $other")
        Logger.trace(s"    ${other.info.authorId} != ${request.solution.info.authorId} && ${other.battleIds.isEmpty}")

        if (other.info.authorId != request.solution.info.authorId && other.battleIds.isEmpty) {

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

    // We have battleIds in solution, should filter in DAO call for solutions with no battles.
    val possibleCompetitors = db.solution.allWithParams(
      status = List(SolutionStatus.InRotation),
      questIds = List(solution.info.questId),
      cultureId = Some(solution.cultureId))

    selectCompetitor(possibleCompetitors) match {
      case Some(competitor) =>

        Logger.trace(s"  Selected competitor solution $competitor}")
        val solutions = List(solution, competitor)

        // FIX: transaction should be here as this operation is atomic.
        val battle = Battle(
          info = BattleInfo(
            battleSides = solutions.map { s =>
              BattleSide(
                solutionId = s.id,
                authorId = s.info.authorId
              )
            },
            questId = solution.info.questId,
            voteEndDate = BattleLogic.voteEndDate(solution.questLevel)
          ),
          level = competitor.questLevel,
          vip = competitor.info.vip || solution.info.vip,
          cultureId = solution.cultureId
        )
        db.battle.create(battle)

        Logger.trace(s"  Battle created")

        solutions.foreach { s =>

          db.solution.addParticipatedBattle(
            id = s.id,
            battleId = battle.id
          )

          db.user.readById(s.info.authorId) ifSome { u =>
            db.user.recordBattleParticipation(u.id, battle.id, SolutionsInBattle(solutions.map(_.id))) ifSome { u =>
              {
                addToTimeLine(AddToTimeLineRequest(
                  user = u,
                  reason = TimeLineReason.Created,
                  objectType = TimeLineType.Battle,
                  objectId = battle.id))
              } map { r =>
                addToWatchersTimeLine(AddToWatchersTimeLineRequest(
                  user = u,
                  reason = TimeLineReason.Created,
                  objectType = TimeLineType.Quest,
                  objectId = battle.id))
              }
            }
          }
        }

        OkApiResult(TryCreateBattleResult())

      case None =>
        Logger.trace(s"  Competitor not selected")
        OkApiResult(TryCreateBattleResult())
    }
  }

  /**
   * Rewards all battle participants.
   * @param request Request with battle.
   * @return Result.
   */
  def rewardBattleParticipants(request: RewardBattleParticipantsRequest): ApiResult[RewardBattleParticipantsResult] = handleDbException {
    import request._

    require(battle.info.status == BattleStatus.Resolved, "Only battles in Resolved state should be passed here")

    db.quest.readById(battle.info.questId) ifSome { q =>
      battle.info.battleSides.foldLeft[ApiResult[_]](OkApiResult()) {
        case (OkApiResult(_), bs) =>
          db.user.readById(bs.authorId) ifSome { user =>
            storeBattleInDailyResult(StoreBattleInDailyResultRequest(
              user = user,
              battle = battle,
              reward = if (bs.isWinner) q.info.victoryReward else q.info.defeatReward))
          }

        case (_ @ result, _) => result
      }
    } map OkApiResult(RewardBattleParticipantsResult())
  }
}

