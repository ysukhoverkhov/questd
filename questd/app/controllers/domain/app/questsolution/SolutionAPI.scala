package controllers.domain.app.questsolution

import models.domain._
import components._
import controllers.domain._
import controllers.domain.helpers._
import controllers.domain.app.user._
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
    import request._
    import ContentVote._

    Logger.debug("API - voteQuestSolutionUpdate")

    def checkInc[T](v: T, c: T, n: Int = 0) = if (v == c) n + 1 else n

    {
      db.solution.updatePoints(
        solution.id,

        reviewsCountChange = 1,
        pointsRandomChange = if (isFriend) 0 else checkInc(vote, Cool),
        pointsFriendsChange = if (isFriend) checkInc(vote, Cool) else 0,
        cheatingChange = checkInc(vote, Cheating),

        spamChange = checkInc(vote, IASpam),
        pornChange = checkInc(vote, IAPorn))
    } ifSome { o =>

      updateQuestSolutionState(UpdateSolutionStateRequest(o)) ifOk
      // TODO: update battle state if on voting.
      // TODO: buttle state update should be in similar API as this one.
        OkApiResult(VoteSolutionUpdateResult())
    }
  }

  /**
   * Update state of quest solution with votes.
   */
  def updateQuestSolutionState(request: UpdateSolutionStateRequest): ApiResult[UpdateSolutionStateResult] = handleDbException {
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
              Logger.error("Unable to find author of quest solution user " + authorId)
              InternalErrorApiResult()
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


