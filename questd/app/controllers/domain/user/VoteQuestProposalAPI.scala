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
    import request._

    user.canVoteQuestProposal match {
      case OK => {
        // 1. get quest to vote.
        // 2. update quest params.
        // 3. check change quest state
        // 4. save quest in db.
        // 5. Update quest author.
        db.quest.readByID(user.profile.questProposalVoteContext.reviewingQuest.get.id) match {
          case None => Logger.error("Unable to find quest with id for voting " + user.profile.questProposalVoteContext.reviewingQuest.get.id)
          case Some(q) => {
            
            voteQuest(VoteQuestUpdateRequest(q, vote, duration, difficulty))
          }
        }

        // 5. update user profile.
        // 6. save profile in db.
        val u = user.copy(
          profile = user.profile.copy(
            questProposalVoteContext = user.profile.questProposalVoteContext.copy(
              numberOfReviewedQuests = user.profile.questProposalVoteContext.numberOfReviewedQuests + 1,
              reviewingQuest = None)))
        db.user.update(u)

        val reward = user.getQuestProposalVoteReward
        adjustAssets(AdjustAssetsRequest(user = u, reward = Some(reward)))

        OkApiResult(Some(VoteQuestProposalResult(OK, Some(u.profile), Some(reward))))
      }
      case a => OkApiResult(Some(VoteQuestProposalResult(a)))
    }
  }

}


