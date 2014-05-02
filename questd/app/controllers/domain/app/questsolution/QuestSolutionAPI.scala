package controllers.domain.app.questsolution

import models.domain._
import models.store._
import components._
import controllers.domain._
import controllers.domain.helpers.exceptionwrappers._
import controllers.domain.app.user._
import logic._
import play.Logger
import models.domain.QuestSolutionVote._

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

    Logger.debug("API - voteQuestSolutionUpdate")

    def checkInc[T](v: T, c: T, n: Int = 0) = if (v == c) n + 1 else n

    val q = db.solution.updatePoints(
      solution.id,

      reviewsCountChange = 1,
      pointsRandomChange = checkInc(vote, Cool),
      pointsFriendsChange = 0,
      pointsInvitedChange = 0,
      cheatingChange = checkInc(vote, Cheating),

      spamChange = checkInc(vote, IASpam),
      pornChange = checkInc(vote, IAPorn))

    updateQuestSolutionState(UpdateQuestSolutionStateRequest(q.get)) map
      OkApiResult(Some(VoteQuestSolutionUpdateResult()))
  }

  /**
   * Update state of quest solution with votes.
   */
  def updateQuestSolutionState(request: UpdateQuestSolutionStateRequest): ApiResult[UpdateQuestSolutionStateResult] = handleDbException {
    import request._

    Logger.debug("API - updateQuestSolutionState")

    def checkWaitCompetitor(qs: QuestSolution) = {
      if (qs.shouldStopVoting)
        db.solution.updateStatus(solution.id, QuestSolutionStatus.WaitingForCompetitor.toString)

      qs
    }

    def checkCheatingSolution(qs: QuestSolution) = {
      if (qs.shouldBanCheating)
        db.solution.updateStatus(solution.id, QuestSolutionStatus.CheatingBanned.toString)

      qs
    }

    def checkAICSolution(qs: QuestSolution) = {
      if (qs.shouldBanIAC)
        db.solution.updateStatus(solution.id, QuestSolutionStatus.IACBanned.toString)

      qs
    }

    val updatedSolution =
      checkCheatingSolution(
        checkAICSolution(
          checkWaitCompetitor(
            solution)))

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


