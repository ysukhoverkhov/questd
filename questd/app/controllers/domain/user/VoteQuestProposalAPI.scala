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
import controllers.domain.DomainAPIComponent
import controllers.domain.InternalErrorApiResult

case class GetQuestProposalToVoteRequest(user: User)
case class GetQuestProposalToVoteResult(allowed: ProfileModificationResult, profile: Option[Profile] = None)

case class VoteQuestProposalRequest(user: User, vote: QuestProposalVote.Value, duration: Option[QuestDuration.Value] = None, difficulty: Option[QuestDifficulty.Value] = None)
case class VoteQuestProposalResult(allowed: ProfileModificationResult, profile: Option[Profile] = None, reward: Option[Assets] = None)

private[domain] trait VoteQuestProposalAPI { this: DomainAPIComponent#DomainAPI with DBAccessor =>

  /**
   * Get cost of quest to shuffle.
   */
  def getQuestProposalToVote(request: GetQuestProposalToVoteRequest): ApiResult[GetQuestProposalToVoteResult] = handleDbException {
    import request._

    user.canGetQuestProposalForVote match {
      case OK => {

        // Updating user profile.
        val q = user.getQuestProposalToVote

        q match {
          case None => OkApiResult(Some(GetQuestProposalToVoteResult(OutOfContent)))
          case Some(a) => {
            val qi = Some(QuestInfoWithID(a.id, a.info))

            val u = user.copy(
              profile = user.profile.copy(
                questProposalVoteContext = user.profile.questProposalVoteContext.copy(
                  reviewingQuest = qi)))

            db.user.update(u)

            OkApiResult(Some(GetQuestProposalToVoteResult(OK, Some(u.profile))))
          }
        }

      }
      case a => OkApiResult(Some(GetQuestProposalToVoteResult(a)))
    }

  }

  /**
   * Get cost of quest to shuffle.
   */
  def voteQuestProposal(request: VoteQuestProposalRequest): ApiResult[VoteQuestProposalResult] = handleDbException {

    request.user.canVoteQuestProposal match {
      case OK => {
        // 1. get quest to vote.
        // 2. vote it.
        val reward = request.user.getQuestProposalVoteReward
        
        db.quest.readByID(request.user.profile.questProposalVoteContext.reviewingQuest.get.id) match {
          case None => {
            Logger.error("Unable to find quest with id for voting " + request.user.profile.questProposalVoteContext.reviewingQuest.get.id)
            InternalErrorApiResult()
          }
          case Some(q) => {
            voteQuest(VoteQuestUpdateRequest(q, request.vote, request.duration, request.difficulty)) map {
              adjustAssets(AdjustAssetsRequest(user = request.user, reward = Some(reward)))
            } map { r =>
              // 3. update user profile.
              // 4. save profile in db.
              val u = r.user.copy(
                profile = r.user.profile.copy(
                  questProposalVoteContext = r.user.profile.questProposalVoteContext.copy(
                    numberOfReviewedQuests = r.user.profile.questProposalVoteContext.numberOfReviewedQuests + 1,
                    reviewingQuest = None)))
              db.user.update(u)

              OkApiResult(Some(VoteQuestProposalResult(OK, Some(u.profile), Some(reward))))
            }
          }
        }

      }
      case a => OkApiResult(Some(VoteQuestProposalResult(a)))
    }
  }

}


