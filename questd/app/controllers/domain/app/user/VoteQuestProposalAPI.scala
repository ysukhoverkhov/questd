package controllers.domain.app.user

import components._
import controllers.domain._
import controllers.domain.app.quest._
import controllers.domain.helpers.exceptionwrappers._
import logic._
import models.domain._
import models.domain.base.QuestInfoWithID
import controllers.domain.app.protocol.ProfileModificationResult._
import play.Logger
import controllers.domain._

case class GetQuestProposalToVoteRequest(user: User)
case class GetQuestProposalToVoteResult(allowed: ProfileModificationResult, profile: Option[Profile] = None)

case class VoteQuestProposalRequest(user: User, vote: QuestProposalVote.Value, duration: Option[QuestDuration.Value] = None, difficulty: Option[QuestDifficulty.Value] = None)
case class VoteQuestProposalResult(allowed: ProfileModificationResult, profile: Option[Profile] = None, reward: Option[Assets] = None, author: Option[Profile] = None)

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
            val qi = QuestInfoWithID(a.id, a.info)
            val theme = db.theme.readByID(a.themeID)

            if (theme == None) {
              Logger.error("API - getQuestProposalToVote. Unable to find theme for quest.")
              InternalErrorApiResult()
            } else {
              val u = db.user.selectQuestProposalVote(user.id, qi, theme.get)
              OkApiResult(Some(GetQuestProposalToVoteResult(OK, u.map(_.profile))))
            }
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
            {
              voteQuest(VoteQuestUpdateRequest(q, request.vote, request.duration, request.difficulty))
            } map {
              rememberProposalVotingInHistory(RememberProposalVotingRequest(request.user, q.id))
            } map {

              adjustAssets(AdjustAssetsRequest(user = request.user, reward = Some(reward)))
            } map { r =>
              // 3. update user profile.
              // 4. save profile in db.
              val u = db.user.recordQuestProposalVote(r.user.id)

              val author = if (request.vote == QuestProposalVote.Cool) {
                db.user.readByID(q.authorUserID).map(_.profile)
              } else {
                None
              }

              OkApiResult(Some(VoteQuestProposalResult(OK, u.map(_.profile), Some(reward), author)))
            }
          }
        }

      }
      case a => OkApiResult(Some(VoteQuestProposalResult(a)))
    }
  }

}


