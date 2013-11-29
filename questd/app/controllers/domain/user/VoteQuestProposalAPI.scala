package controllers.domain.user

import components.DBAccessor
import controllers.domain.ApiResult
import controllers.domain.OkApiResult
import controllers.domain.helpers.exceptionwrappers.handleDbException
import logic.user2Logic
import models.domain.Assets
import models.domain.Profile
import models.domain.User
import models.domain.base.QuestInfoWithID
import protocol.ProfileModificationResult.OK
import protocol.ProfileModificationResult.OutOfContent
import protocol.ProfileModificationResult.ProfileModificationResult

case class GetQuestToVoteRequest(user: User)
case class GetQuestToVoteResult(allowed: ProfileModificationResult, profile: Option[Profile] = None)

// TODO make quest duration not integer in days but enum (after implementing voting).

object QuestProposalVote extends Enumeration {
  val Cool, SoSo, Cheating, IASpam, IAPorn = Value
}

case class VoteQuestRequest(user: User, vote: QuestProposalVote.Value)
case class VoteQuestResult(allowed: ProfileModificationResult, profile: Option[Profile] = None, reward: Option[Assets] = None)

private[domain] trait VoteQuestProposalAPI { this: DBAccessor =>

  /**
   * Get cost of quest to shuffle.
   */
  def getQuestToVote(request: GetQuestToVoteRequest): ApiResult[GetQuestToVoteResult] = handleDbException {
    import request._

    user.canGetQuestForVote match {
      case OK => {

        // Updating user profile.
        val q = user.getQuestToVote

        q match {
          case None => OkApiResult(Some(GetQuestToVoteResult(OutOfContent)))
          case Some(a) => {
            val qi = Some(QuestInfoWithID(a.id, a.info))

            val u = user.copy(
              profile = user.profile.copy(
                questVoteContext = user.profile.questVoteContext.copy(
                  reviewingQuest = qi)))

            db.user.update(u)

            OkApiResult(Some(GetQuestToVoteResult(OK, Some(u.profile))))
          }
        }

      }
      case a => OkApiResult(Some(GetQuestToVoteResult(a)))
    }

  }

  /**
   * Get cost of quest to shuffle.
   */
  def voteQuestProposal(request: VoteQuestRequest): ApiResult[VoteQuestResult] = handleDbException {
    import request._

    user.canVoteQuest match {
      case OK => {
        
        

        OkApiResult(Some(VoteQuestResult(OK, Some(user.profile))))

      }
      case a => OkApiResult(Some(VoteQuestResult(a)))
    }
  }

}


