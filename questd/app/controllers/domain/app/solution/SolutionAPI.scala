package controllers.domain.app.solution

import components._
import controllers.domain._
import controllers.domain.app.user._
import controllers.domain.helpers._
import models.domain.battle.BattleStatus
import models.domain.common.ContentVote
import models.domain.solution.{Solution, SolutionStatus}
import play.Logger

case class VoteSolutionRequest(
  solution: Solution,
  isFriend: Boolean,
  vote: ContentVote.Value)
case class VoteSolutionResult()

case class UpdateSolutionStateRequest(solution: Solution)
case class UpdateSolutionStateResult(solution: Solution)

case class SelectSolutionToTimeLineRequest(solution: Solution)
case class SelectSolutionToTimeLineResult(solution: Solution)


private[domain] trait SolutionAPI { this: DomainAPIComponent#DomainAPI with DBAccessor =>

  /**
   * Updates solution according to vote.
   */
  def voteSolution(request: VoteSolutionRequest): ApiResult[VoteSolutionResult] = handleDbException {
    import ContentVote._
    import request._

    Logger.debug("API - voteSolution")

    def checkInc[T](v: T, c: T, n: Int = 0) = if (v == c) n + 1 else n

    def solutionInBattle(solution: Solution) = {
      solution.battleIds.foldLeft(false) {
        case (true, _) => true
        case (_, battleId) =>
          db.battle.readById(battleId).fold {
            Logger.error(s"unable to find battle with id $battleId for determining solution battle status")
            false
          } { battle =>
            battle.info.status == BattleStatus.Fighting
          }
      }
    }

    if (vote == Cool || !solutionInBattle(solution)) {
      {
        db.solution.updatePoints(
          solution.id,

          votersCountChange = 1,
          timelinePointsChange = checkInc(vote, Cool),
          likesChange = checkInc(vote, Cool),

          cheatingChange = checkInc(vote, Cheating),
          spamChange = checkInc(vote, IASpam),
          pornChange = checkInc(vote, IAPorn))
      } ifSome { o =>

        updateSolutionState(UpdateSolutionStateRequest(o)) map {
          OkApiResult(VoteSolutionResult())
        }
      }
    } else {
      OkApiResult(VoteSolutionResult())
    }
  }

  /**
   * Do everything required with solution when it's selected to timeline.
   */
  def selectSolutionToTimeLine(request: SelectSolutionToTimeLineRequest): ApiResult[SelectSolutionToTimeLineResult] = handleDbException {
    import request._

    {
      db.solution.updatePoints(solution.id, timelinePointsChange = -1)
    } ifSome { v =>
      updateSolutionState(UpdateSolutionStateRequest(v))
    } map { r =>
      OkApiResult(SelectSolutionToTimeLineResult(r.solution))
    }
  }

  /**
   * Update state of quest solution with votes.
   */
  def updateSolutionState(request: UpdateSolutionStateRequest): ApiResult[UpdateSolutionStateResult] = handleDbException {
    import request._

    Logger.debug("API - updateQuestSolutionState")

    def checkCheatingSolution(qs: Solution) = {
      if (qs.shouldBanCheating)
        db.solution.updateStatus(solution.id, SolutionStatus.CheatingBanned)
      else
        Some(qs)
    }

    def checkAICSolution(qs: Solution) = {
      if (qs.shouldBanIAC)
        db.solution.updateStatus(solution.id, SolutionStatus.IACBanned)
      else
        Some(qs)
    }

    val functions = List(
      checkCheatingSolution _,
      checkAICSolution _)

    functions.foldLeft[Option[Solution]](Some(solution))((r, f) => {
      r.flatMap(f)
    }) ifSome { s =>
      val authorUpdateResult =
        if (solution.status != s.status) {
          val authorId = s.info.authorId

          db.user.readById(authorId) match {
            case None =>
              InternalErrorApiResult(s"Unable to find author of quest solution user $authorId")
            case Some(author) =>
              rewardSolutionAuthor(RewardSolutionAuthorRequest(solution = s, author = author))
          }
        } else {
          OkApiResult(None)
        }

      authorUpdateResult map OkApiResult(UpdateSolutionStateResult(s))
    }
  }
}


