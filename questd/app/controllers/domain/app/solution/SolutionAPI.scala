package controllers.domain.app.solution

import components._
import controllers.domain._
import controllers.domain.app.user._
import controllers.domain.helpers._
import models.domain.common.ContentVote
import models.domain.solution.{Solution, SolutionStatus}
import play.Logger

case class VoteSolutionRequest(
  solution: Solution,
  isFriend: Boolean,
  vote: ContentVote.Value)
case class VoteSolutionResult()

case class UpdateSolutionStateRequest(solution: Solution)
case class UpdateSolutionStateResult()

private[domain] trait SolutionAPI { this: DomainAPIComponent#DomainAPI with DBAccessor =>

  /**
   * Updates solution according to vote.
   */
  def voteSolution(request: VoteSolutionRequest): ApiResult[VoteSolutionResult] = handleDbException {
    import ContentVote._
    import request._

    Logger.debug("API - voteSolution")

    def checkInc[T](v: T, c: T, n: Int = 0) = if (v == c) n + 1 else n

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
  }

  /**
   * Update state of quest solution with votes.
   */
  def updateSolutionState(request: UpdateSolutionStateRequest): ApiResult[UpdateSolutionStateResult] = handleDbException {
    import request._

    Logger.debug("API - updateQuestSolutionState")

    def checkCheatingSolution(qs: Solution) = {
      if (qs.shouldBanCheating)
        db.solution.updateStatus(solution.id, Some(SolutionStatus.CheatingBanned))
      else
        Some(qs)
    }

    def checkAICSolution(qs: Solution) = {
      if (qs.shouldBanIAC)
        db.solution.updateStatus(solution.id, Some(SolutionStatus.IACBanned))
      else
        Some(qs)
    }

    val functions = List(
      checkCheatingSolution _,
      checkAICSolution _)

    val updatedSolution = functions.foldLeft[Option[Solution]](Some(solution))((r, f) => {
      r.flatMap(f)
    })

    updatedSolution ifSome { s =>
      val authorUpdateResult =
        if (s.status != solution.status) {
          val authorId = solution.info.authorId

          db.user.readById(authorId) match {
            case None =>
              InternalErrorApiResult(s"Unable to find author of quest solution user $authorId")
            case Some(author) =>
              rewardSolutionAuthor(RewardSolutionAuthorRequest(solution = s, author = author))
          }
        } else {
          OkApiResult(None)
        }

      authorUpdateResult map OkApiResult(UpdateSolutionStateResult())
    }
  }
}


