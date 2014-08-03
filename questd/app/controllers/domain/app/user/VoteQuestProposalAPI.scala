package controllers.domain.app.user

import components._
import controllers.domain._
import controllers.domain.app.quest._
import controllers.domain.helpers._
import logic._
import models.domain._
import models.domain.base.QuestInfoWithID
import controllers.domain.app.protocol.ProfileModificationResult._
import play.Logger
import controllers.domain._
import models.domain.base.PublicProfileWithID
import models.domain.base.PublicProfileWithID

case class GetQuestProposalToVoteRequest(user: User)
case class GetQuestProposalToVoteResult(allowed: ProfileModificationResult, profile: Option[Profile] = None)

case class VoteQuestProposalRequest(user: User, vote: QuestProposalVote.Value, duration: QuestDuration.Value, difficulty: QuestDifficulty.Value)
case class VoteQuestProposalResult(allowed: ProfileModificationResult, profile: Option[Profile] = None, reward: Option[Assets] = None, author: Option[PublicProfileWithID] = None)

private[domain] trait VoteQuestProposalAPI { this: DomainAPIComponent#DomainAPI with DBAccessor =>

  /**
   * Get cost of quest to shuffle.
   */
  def getQuestProposalToVote(request: GetQuestProposalToVoteRequest): ApiResult[GetQuestProposalToVoteResult] = handleDbException {
    import request._

    user.canGetQuestProposalForVote match {
      case OK => {

        Logger.trace("getQuestProposalToVote - we are eligable to vote quest.")

        // Updating user profile.
        val q = user.getQuestProposalToVote

        q match {
          case None => OkApiResult(GetQuestProposalToVoteResult(OutOfContent))
          case Some(a) => {
            val qi = QuestInfoWithID(a.id, a.info)
            val theme = db.theme.readById(a.info.themeId)

            if (theme == None) {
              Logger.error("API - getQuestProposalToVote. Unable to find theme for quest.")
              InternalErrorApiResult()
            } else {
              val u = db.user.selectQuestProposalVote(user.id, qi, theme.get)
              OkApiResult(GetQuestProposalToVoteResult(OK, u.map(_.profile)))
            }
          }
        }
      }
      case a => OkApiResult(GetQuestProposalToVoteResult(a))
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

        db.quest.readById(request.user.profile.questProposalVoteContext.reviewingQuest.get.id) match {
          case None => {
            Logger.error("Unable to find quest with id for voting " + request.user.profile.questProposalVoteContext.reviewingQuest.get.id)
            InternalErrorApiResult()
          }
          case Some(q) => {
            {
              
              voteQuest(VoteQuestUpdateRequest(q, request.vote, request.duration, request.difficulty))
              
            } ifOk { r =>
              
              makeTask(MakeTaskRequest(request.user, taskType = Some(TaskType.VoteQuestProposals)))
              
            } ifOk {

              adjustAssets(AdjustAssetsRequest(user = request.user, reward = Some(reward)))

            } ifOk { r =>

              val liked = (request.vote == QuestProposalVote.Cool)
              val u = db.user.recordQuestProposalVote(r.user.id, q.id, liked)

              val author = if (liked) {
                // TODO: make here if Some
                val a = db.user.readById(q.authorUserId)
                if (a != None) {
                  Some(PublicProfileWithID(a.get.id, a.get.profile.publicProfile))
                } else {
                  None
                }
              } else {
                None
              }

              OkApiResult(VoteQuestProposalResult(OK, u.map(_.profile), Some(reward), author))
            }
          }
        }

      }
      case a => OkApiResult(VoteQuestProposalResult(a))
    }
  }

}


