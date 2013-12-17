package controllers.domain.user

import models.domain._
import models.store._
import controllers.domain.DomainAPIComponent
import components._
import controllers.domain._
import controllers.domain.helpers.exceptionwrappers._
import logic._
import play.Logger

case class VoteQuestSolutionUpdateRequest(
  solution: QuestSolution,
  vote: QuestSolutionVote.Value)
case class VoteQuestSolutionUpdateResult()

case class UpdateQuestSolutionStateRequest(solution: QuestSolution)
case class UpdateQuestSolutionStateResult()

private[domain] trait QuestSolutionAPI { this: DomainAPIComponent#DomainAPI with DBAccessor =>

  /**
   * Updates quest according to vote.
   */
  def voteQuestSolutionUpdate(request: VoteQuestSolutionUpdateRequest): ApiResult[VoteQuestSolutionUpdateResult] = handleDbException {
    import request._
    import QuestSolutionVote._

    def checkInc[T](v: T, c: T, n: Int) = if (v == c) n + 1 else n

    val q2 = solution.copy(
      rating = solution.rating.copy(
        reviewsCount = solution.rating.reviewsCount + 1,
        pointsRandom = checkInc(vote, Cool, solution.rating.pointsRandom),
        cheating = checkInc(vote, Cheating, solution.rating.cheating),
        iacpoints = solution.rating.iacpoints.copy(
          spam = checkInc(vote, IASpam, solution.rating.iacpoints.spam),
          porn = checkInc(vote, IAPorn, solution.rating.iacpoints.porn))))

    db.solution.update(q2)

    updateQuestSolutionState(UpdateQuestSolutionStateRequest(q2)) map
      OkApiResult(Some(VoteQuestSolutionUpdateResult()))

  }

  /**
   * Update state of quest solution with votes.
   */
  def updateQuestSolutionState(request: UpdateQuestSolutionStateRequest): ApiResult[UpdateQuestSolutionStateResult] = handleDbException {
    import request._

    def checkWaitCompetitor(qs: QuestSolution) = {
      if (qs.shouldStopVoting)
        qs.copy(status = QuestSolutionStatus.WaitingForCompetitor.toString)
      else
        qs
    }

    def checkCheatingSolution(qs: QuestSolution) = {
      if (qs.shouldBanCheating)
        qs.copy(status = QuestSolutionStatus.CheatingBanned.toString)
      else
        qs
    }

    def checkAICSolution(qs: QuestSolution) = {
      if (qs.shouldBanIAC)
        qs.copy(status = QuestSolutionStatus.IACBanned.toString)
      else
        qs
    }

    val updatedSolution =
      checkCheatingSolution(
        checkAICSolution(
          checkWaitCompetitor(
            solution)))

    db.solution.update(updatedSolution)

    val authorUpdateResult =
      if (updatedSolution.status != solution.status) {
        val authorID = solution.userID
        db.user.readByID(authorID) match {
          case None => {
            Logger.error("Unable to find author of quest solution user " + authorID)
            InternalErrorApiResult()
          }
          case Some(author) => {
            rewardQuestSolutionAuthor(RewardQuestSolutionAuthorRequest(updatedSolution, author))
          }
        }
      } else {
        OkApiResult(None)
      }

    authorUpdateResult map OkApiResult(Some(UpdateQuestSolutionStateResult()))
  }

}


