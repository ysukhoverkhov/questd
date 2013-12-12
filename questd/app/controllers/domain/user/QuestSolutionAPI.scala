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
        pointsRandom = checkInc(vote, Cool, solution.rating.pointsRandom),
        cheating = checkInc(vote, Cheating, solution.rating.cheating),
        iacpoints = solution.rating.iacpoints.copy(
          spam = checkInc(vote, IASpam, solution.rating.iacpoints.spam),
          porn = checkInc(vote, IAPorn, solution.rating.iacpoints.porn))))

    db.solution.update(q2)

    OkApiResult(Some(VoteQuestSolutionUpdateResult()))
  }

}

