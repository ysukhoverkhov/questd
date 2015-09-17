package controllers.domain.app.user

import components._
import controllers.domain._
import controllers.domain.app.protocol.ProfileModificationResult
import controllers.domain.helpers._
import logic.BattleLogic
import models.domain.battle.{Battle, BattleInfo, BattleSide, BattleStatus}
import models.domain.challenge.{ChallengeStatus, Challenge}
import models.domain.solution.{Solution, SolutionStatus}
import models.domain.user.User
import models.domain.user.stats.SolutionsInBattle
import models.domain.user.timeline.{TimeLineReason, TimeLineType}
import play.Logger

import scala.language.postfixOps

case class CreateBattleRequest(solutions: List[Solution])
case class CreateBattleResult()

case class TryCreateBattleRequest(solution: Solution)
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
    def selectCompetitorSolution(possibleCompetitorSolutions: Iterator[Solution], author: User, exclusive: Boolean, checkQuest: Boolean): Option[Solution] = {
      if (possibleCompetitorSolutions.hasNext) {
        val other = possibleCompetitorSolutions.next()

        Logger.trace(s"    Analysing competitor solution ${other.id} - $other")
        Logger.trace(s"    ${other.info.authorId} != ${solution.info.authorId} && (${other.battleIds.isEmpty} && $exclusive)")

        if (author.canAutoCreatedBattle (solution, other, exclusive, checkQuest) == ProfileModificationResult.OK) {

          Logger.trace("Found fight pair for quest " + solution.info.questId + " :")
          Logger.trace("  s1.id=" + solution.id)
          Logger.trace("  s2.id=" + other.id)

          Some(other)

        } else {
          // Skipping to next if current is we are.
          selectCompetitorSolution(possibleCompetitorSolutions, author, exclusive, checkQuest)
        }
      } else {
        None
      }
    }

    /**
     * Selects possible rivals with statuses.
     */
    def solutionsForStatus(
      status: SolutionStatus.Value,
      questId: Option[String],
      withBattles: Option[Boolean]): Iterator[Solution] = {
      db.solution.allWithParams(
        status = List(status),
        questIds = questId.fold[List[String]](List.empty){questId => List(questId)},
        cultureId = Some(solution.cultureId),
        withBattles = withBattles)
    }
// TODO: test new version of makeChallenge is working.
    def makeChallenge(solutions: List[Solution]): Unit = {
      val mySolution = solutions.head
      val opponentSolution = solutions(1)
      val myId = mySolution.info.authorId
      val opponentId = opponentSolution.info.authorId

      db.challenge.create(Challenge(
        myId = myId,
        opponentId = opponentId,
        mySolutionId = Some(mySolution.id),
        opponentSolutionId = Some(opponentSolution.id),
        status = ChallengeStatus.AutoCreated))
    }

    if (solution.canParticipateAutoBattle) {
      db.user.readById(solution.info.authorId) ifSome { author =>
        val solutions = selectCompetitorSolution(
          solutionsForStatus(SolutionStatus.InRotation, Some(solution.info.questId), withBattles = Some(false)),
          author,
          exclusive = true,
          checkQuest = true) match {
          case Some(competitorSolution) =>

            Logger.trace(s"  Selected competitor solution $competitorSolution}")
            List(solution, competitorSolution)

          case None =>
            Logger.trace(s"  Competitor not selected")
            List.empty
        }

        if (solutions.isEmpty) {
          OkApiResult(TryCreateBattleResult())
        } else {
          makeChallenge(solutions)
          createBattle(CreateBattleRequest(solutions)) map OkApiResult(TryCreateBattleResult())
        }
      }
    } else {
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

