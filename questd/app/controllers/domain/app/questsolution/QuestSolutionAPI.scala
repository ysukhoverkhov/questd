package controllers.domain.app.questsolution

import models.domain._
import components._
import controllers.domain._
import controllers.domain.helpers._
import controllers.domain.app.user._
import play.Logger

case class VoteQuestSolutionUpdateRequest(
  solution: QuestSolution,
  vote: ContentVote.Value)
case class VoteQuestSolutionUpdateResult()

case class UpdateQuestSolutionStateRequest(solution: QuestSolution)
case class UpdateQuestSolutionStateResult()

private[domain] trait QuestSolutionAPI { this: DomainAPIComponent#DomainAPI with DBAccessor =>

  /**
   * Updates quest according to vote.
   */
  def voteQuestSolutionUpdate(request: VoteQuestSolutionUpdateRequest): ApiResult[VoteQuestSolutionUpdateResult] = handleDbException {
    import request._
    import ContentVote._

    Logger.debug("API - voteQuestSolutionUpdate")

    def checkInc[T](v: T, c: T, n: Int = 0) = if (v == c) n + 1 else n

    {
      db.solution.updatePoints(
        solution.id,

        reviewsCountChange = 1,
        pointsRandomChange = checkInc(vote, Cool),
        pointsFriendsChange = 0,
        pointsInvitedChange = 0,
        cheatingChange = checkInc(vote, Cheating),

        spamChange = checkInc(vote, IASpam),
        pornChange = checkInc(vote, IAPorn))
    } ifSome { o =>

      updateQuestSolutionState(UpdateQuestSolutionStateRequest(o)) ifOk
        OkApiResult(VoteQuestSolutionUpdateResult())
    }
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
      else
        Some(qs)
    }

    def checkCheatingSolution(qs: QuestSolution) = {
      if (qs.shouldBanCheating)
        db.solution.updateStatus(solution.id, QuestSolutionStatus.CheatingBanned.toString)
      else
        Some(qs)
    }

    def checkAICSolution(qs: QuestSolution) = {
      if (qs.shouldBanIAC)
        db.solution.updateStatus(solution.id, QuestSolutionStatus.IACBanned.toString)
      else
        Some(qs)
    }

    val funcs = List(
      checkWaitCompetitor _,
      checkCheatingSolution _,
      checkAICSolution _)

    val updatedSolution = funcs.foldLeft[Option[QuestSolution]](Some(solution))((r, f) => {
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
              rewardQuestSolutionAuthor(RewardQuestSolutionAuthorRequest(s, author))
          }
        } else {
          OkApiResult(None)
        }

      authorUpdateResult ifOk OkApiResult(UpdateQuestSolutionStateResult())
    }
  }
}


