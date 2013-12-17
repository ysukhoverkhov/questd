package controllers.domain.user

import components._
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
import controllers.domain.DomainAPIComponent
import controllers.domain.InternalErrorApiResult

case class GetQuestSolutionToVoteRequest(user: User)
case class GetQuestSolutionToVoteResult(allowed: ProfileModificationResult, profile: Option[Profile] = None)

case class VoteQuestSolutionRequest(user: User, vote: QuestSolutionVote.Value)
case class VoteQuestSolutionResult(allowed: ProfileModificationResult, profile: Option[Profile] = None, reward: Option[Assets] = None)

private[domain] trait VoteQuestSolutionAPI { this: DomainAPIComponent#DomainAPI with DBAccessor =>

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

    Logger.debug("API - voteQuestSolution")
    
    request.user.canVoteQuestSolution match {
      case OK => {
        // 1. get quest to vote.
        // 2. update quest params.
        // 3. check change quest state
        // 4. save quest in db.
        val reward = request.user.getQuestSolutionVoteReward

        db.solution.readByID(request.user.profile.questSolutionVoteContext.reviewingQuestSolution.get.id) match {
          case None => {
            Logger.error("Unable to find quest solution with id for voting " + request.user.profile.questSolutionVoteContext.reviewingQuestSolution.get.id)
            InternalErrorApiResult()
          }
          case Some(q) => {
            voteQuestSolutionUpdate(VoteQuestSolutionUpdateRequest(q, request.vote)) map {
              // 5. update user profile.
              // 6. save profile in db.
              adjustAssets(AdjustAssetsRequest(user = request.user, reward = Some(reward))) map { r =>
                val u = r.user.copy(
                  profile = r.user.profile.copy(
                    questSolutionVoteContext = r.user.profile.questSolutionVoteContext.copy(
                      numberOfReviewedSolutions = r.user.profile.questSolutionVoteContext.numberOfReviewedSolutions + 1,
                      reviewingQuestSolution = None)))
                db.user.update(u)

                OkApiResult(Some(VoteQuestSolutionResult(OK, Some(u.profile), Some(reward))))
              }
            }
          }
        }
      }
      
      case a => OkApiResult(Some(VoteQuestSolutionResult(a)))
    }
  }

}


