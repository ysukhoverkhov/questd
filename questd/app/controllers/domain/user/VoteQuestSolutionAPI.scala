package controllers.domain.user

import components.DBAccessor
import controllers.domain.ApiResult
import controllers.domain.OkApiResult
import controllers.domain.helpers.exceptionwrappers.handleDbException
import logic._
import models.domain._
import models.domain.base.QuestInfoWithID
import protocol.ProfileModificationResult.OK
import protocol.ProfileModificationResult.OutOfContent
import protocol.ProfileModificationResult.ProfileModificationResult
import play.Logger
import models.domain.base._

case class GetQuestSolutionToVoteRequest(user: User)
case class GetQuestSolutionToVoteResult(allowed: ProfileModificationResult, profile: Option[Profile] = None)

case class VoteQuestSolutionRequest(user: User, vote: QuestSolutionVote.Value)
case class VoteQuestSolutionResult(allowed: ProfileModificationResult, profile: Option[Profile] = None, reward: Option[Assets] = None)

private[domain] trait VoteQuestSolutionAPI { this: DBAccessor =>

  /**
   * Get quest solution to vote for.
   */
  def getQuestSolutionToVote(request: GetQuestSolutionToVoteRequest): ApiResult[GetQuestSolutionToVoteResult] = handleDbException {
    import request._

    user.canGetQuestSolutionForVote match {
      case OK => {

        // Updating user profile.
        val q = user.getQuestSolutionToVote

        q match {
          case None => OkApiResult(Some(GetQuestSolutionToVoteResult(OutOfContent)))
          case Some(a) => {
            val qi = Some(QuestSolutionInfoWithID(a.id, a.info))

            val u = user.copy(
              profile = user.profile.copy(
                questSolutionVoteContext = user.profile.questSolutionVoteContext.copy(
                  reviewingQuestSolution = qi)))

            db.user.update(u)

            OkApiResult(Some(GetQuestSolutionToVoteResult(OK, Some(u.profile))))
          }
        }

      }
      case a => OkApiResult(Some(GetQuestSolutionToVoteResult(a)))
    }
  }

  /**
   * Get cost of quest to shuffle.
   */
  def voteQuestSolution(request: VoteQuestSolutionRequest): ApiResult[VoteQuestSolutionResult] = handleDbException {
    import request._

    def updateQuestSolutionWithVote(q: QuestSolution, v: QuestSolutionVote.Value): QuestSolution = {
      import QuestSolutionVote._

      def checkInc[T](v: T, c: T, n: Int) = if (v == c) n + 1 else n

      q.copy(
        rating = q.rating.copy(
          pointsRandom = checkInc(v, Cool, q.rating.pointsRandom),
          cheating = checkInc(v, Cheating, q.rating.cheating),
          iacpoints = q.rating.iacpoints.copy(
            spam = checkInc(v, IASpam, q.rating.iacpoints.spam),
            porn = checkInc(v, IAPorn, q.rating.iacpoints.porn))))
    }

    user.canVoteQuestSolution match {
      case OK => {
        // 1. get quest to vote.
        // 2. update quest params.
        // 3. check change quest state
        // 4. save quest in db.
        db.solution.readByID(user.profile.questSolutionVoteContext.reviewingQuestSolution.get.id) match {
          case None => Logger.error("Unable to find quest solution with id for voting " + user.profile.questSolutionVoteContext.reviewingQuestSolution.get.id)
          case Some(q) => {
            db.solution.update(updateQuestSolutionWithVote(q, vote))
          }
        }

        // 5. update user profile.
        // 6. save profile in db.
        val reward = user.getQuestSolutionVoteReward

        val u = user.copy(
          profile = user.profile.copy(
            questSolutionVoteContext = user.profile.questSolutionVoteContext.copy(
              numberOfReviewedSolutions = user.profile.questSolutionVoteContext.numberOfReviewedSolutions + 1,
              reviewingQuestSolution = None)))
          .giveUserReward(reward)

        db.user.update(u)

        OkApiResult(Some(VoteQuestSolutionResult(OK, Some(u.profile), Some(reward))))

      }
      case a => OkApiResult(Some(VoteQuestSolutionResult(a)))
    }
  }

}


