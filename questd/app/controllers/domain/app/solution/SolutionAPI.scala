package controllers.domain.app.solution

import controllers.domain.app.battle.{UpdateBattleStateResult, UpdateBattleStateRequest}
import components._
import controllers.domain._
import controllers.domain.app.user._
import controllers.domain.helpers._
import models.domain._
import play.Logger

case class VoteSolutionUpdateRequest(
  solution: Solution,
  isFriend: Boolean,
  vote: ContentVote.Value)
case class VoteSolutionUpdateResult()

case class UpdateSolutionStateRequest(solution: Solution)
case class UpdateSolutionStateResult()

private[domain] trait SolutionAPI { this: DomainAPIComponent#DomainAPI with DBAccessor =>

  /**
   * Updates quest according to vote.
   */
  def voteSolutionUpdate(request: VoteSolutionUpdateRequest): ApiResult[VoteSolutionUpdateResult] = handleDbException {
    import models.domain.ContentVote._
    import request._

    Logger.debug("API - voteQuestSolutionUpdate")

    def checkInc[T](v: T, c: T, n: Int = 0) = if (v == c) n + 1 else n

    {
      db.solution.updatePoints(
        solution.id,

        reviewsCountChange = 1,
        pointsRandomChange = if (isFriend) 0 else checkInc(vote, Cool),
        pointsFriendsChange = if (isFriend) checkInc(vote, Cool) else 0,
        likesCountChange = checkInc(vote, Cool),

        cheatingChange = checkInc(vote, Cheating),

        spamChange = checkInc(vote, IASpam),
        pornChange = checkInc(vote, IAPorn))
    } ifSome { o =>

      updateSolutionState(UpdateSolutionStateRequest(o)) ifOk {
        (if (o.status == SolutionStatus.OnVoting) {
          db.battle.readById(o.battleIds.head) ifSome { b =>
            updateBattleState(UpdateBattleStateRequest(b))
          }
        } else {
          OkApiResult(UpdateBattleStateResult)
        }) ifOk {
          OkApiResult(VoteSolutionUpdateResult())
        }
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
              rewardSolutionAuthor(RewardSolutionAuthorRequest(s, author))
          }
        } else {
          OkApiResult(None)
        }

      authorUpdateResult ifOk OkApiResult(UpdateSolutionStateResult())
    }
  }
}


