package controllers.domain.app.user

import components._
import controllers.domain._
import controllers.domain.helpers._
import logic.BattleLogic
import models.domain.base.ID
import models.domain.battle.{Battle, BattleInfo, BattleSide, BattleStatus}
import models.domain.solution.{Solution, SolutionRating, SolutionStatus}
import models.domain.user.stats.SolutionsInBattle
import models.domain.user.timeline.{TimeLineReason, TimeLineType}
import play.Logger

import scala.language.postfixOps

case class CreateBattleRequest(solutions: List[Solution])
case class CreateBattleResult()

case class TryCreateBattleRequest(solution: Solution, useTutorialCompetitor: Boolean)
case class TryCreateBattleResult()

case class RewardBattleParticipantsRequest(battle: Battle)
case class RewardBattleParticipantsResult()

private[domain] trait FightBattleAPI { this: DomainAPIComponent#DomainAPI with DBAccessor =>

  /**
   * Creates battle between two solutions.
   */
  def createBattle(request: CreateBattleRequest): ApiResult[CreateBattleResult] = handleDbException {
    import request.solutions

    // FIX: transaction should be here as this operation is atomic.
    val battle = Battle(
      info = BattleInfo(
        battleSides = solutions.map { s =>
          BattleSide(
            solutionId = s.id,
            authorId = s.info.authorId
          )
        },
        questId = solutions.head.info.questId,
        voteEndDate = BattleLogic.voteEndDate(solutions.head.questLevel)
      ),
      level = solutions.head.questLevel,
      vip = solutions.foldLeft(false) { (r, v) => r || v.info.vip},
      cultureId = solutions.head.cultureId
    )
    db.battle.create(battle)

    Logger.trace(s"  Battle created with id ${battle.id}")

    solutions.foreach { s =>

      db.solution.addParticipatedBattle(
        id = s.id,
        battleId = battle.id
      )

      db.user.readById(s.info.authorId) ifSome { u =>
        db.user.recordBattleParticipation(u.id, battle.id, SolutionsInBattle(solutions.map(_.id))) ifSome { u => {
          addToTimeLine(
            AddToTimeLineRequest(
              user = u,
              reason = TimeLineReason.Created,
              objectType = TimeLineType.Battle,
              objectId = battle.id))
        } map { r =>
          addToWatchersTimeLine(
            AddToWatchersTimeLineRequest(
              user = u,
              reason = TimeLineReason.Created,
              objectType = TimeLineType.Battle,
              objectId = battle.id))
        }
        }
      }
    }

    OkApiResult(CreateBattleResult())
  }

  /**
   * Tries to match solution with competitor, leaves it as it is if not found.
   * @param request Request with solution to find competitor for.
   * @return Result of competitor search.
   */
  def tryCreateBattle(request: TryCreateBattleRequest): ApiResult[TryCreateBattleResult] = handleDbException {
    import request._

    Logger.trace(s"Trying to create battle")

    /**
     * Selects out of provided competitors suitable one.
     */
    def selectCompetitorSolution(possibleCompetitorSolutions: Iterator[Solution], exclusive: Boolean): Option[Solution] = {
      if (possibleCompetitorSolutions.hasNext) {
        val other = possibleCompetitorSolutions.next()

        Logger.trace(s"    Analysing competitor solution ${other.id} - $other")
        Logger.trace(s"    ${other.info.authorId} != ${solution.info.authorId} && (${other.battleIds.isEmpty} && $exclusive)")

        if (other.info.authorId != solution.info.authorId && (other.battleIds.isEmpty || !exclusive)) {

          Logger.debug("Found fight pair for quest " + solution.info.questId + " :")
          Logger.debug("  s1.id=" + solution.id)
          Logger.debug("  s2.id=" + other.id)

          Some(other)

        } else {
          // Skipping to next if current is we are.
          selectCompetitorSolution(possibleCompetitorSolutions, exclusive)
        }
      } else {
        None
      }
    }

    /**
     * Selects possible rivals with statuses.
     */
    def solutionsForStatus(status: SolutionStatus.Value, questId: Option[String]): Iterator[Solution] = {
      db.solution.allWithParams(
        status = List(status),
        questIds = questId.fold[List[String]](List.empty){questId => List(questId)},
        cultureId = Some(solution.cultureId))
    }

    selectCompetitorSolution(solutionsForStatus(SolutionStatus.InRotation, Some(solution.info.questId)), exclusive = true) match {
      case Some(competitorSolution) =>

        Logger.trace(s"  Selected competitor solution $competitorSolution}")
        val solutions = List(solution, competitorSolution)

        createBattle(CreateBattleRequest(solutions)) map OkApiResult(TryCreateBattleResult())

      case None =>
        Logger.trace(s"  Competitor not selected, trying to find tutorial one.")

        selectCompetitorSolution(solutionsForStatus(SolutionStatus.ForTutorial, Some(solution.info.questId)), exclusive = false) match {
          case Some(competitorSolution) =>

            Logger.trace(s"  Selected tutorial competitor solution $competitorSolution}")
            val solutions = List(solution, competitorSolution)

            createBattle(CreateBattleRequest(solutions)) map OkApiResult(TryCreateBattleResult())

          case None =>

            if (request.useTutorialCompetitor) {
              selectCompetitorSolution(solutionsForStatus(SolutionStatus.ForTutorial, None), exclusive = false) match {
                case Some(competitorSolution) =>

                  val updatedCompetitorSolution = if (competitorSolution.info.questId != solution.info.questId) {
                    competitorSolution.copy(
                      id = ID.generateUUID(),
                      battleIds = List.empty,
                      rating = SolutionRating(),
                      info = competitorSolution.info.copy(
                        questId = solution.info.questId
                      )
                    )
                  } else {
                    competitorSolution
                  }

                  Logger.trace(s"  Selected tutorial must competitor solution $updatedCompetitorSolution}")
                  val solutions = List(solution, updatedCompetitorSolution)

                  createBattle(CreateBattleRequest(solutions)) map OkApiResult(TryCreateBattleResult())
                case None =>
                  Logger.error(s"  Competitor not selected for solution with useTutorialCompetitor set to $useTutorialCompetitor")
                  OkApiResult(TryCreateBattleResult())
              }
            } else {
              Logger.trace(s"  Competitor not selected")
              OkApiResult(TryCreateBattleResult())
            }
        }
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

