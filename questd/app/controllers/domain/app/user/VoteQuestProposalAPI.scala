package controllers.domain.app.user

import components._
import controllers.domain.app.quest._
import controllers.domain.helpers._
import models.domain._
import models.domain.view._
import controllers.domain.app.protocol.ProfileModificationResult._
import play.Logger
import controllers.domain._

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
      case OK =>

        Logger.trace("getQuestProposalToVote - we are eligable to vote quest.")

        // Updating user profile.
        val q = user.getQuestProposalToVote

        q match {
          case None => OkApiResult(GetQuestProposalToVoteResult(OutOfContent))
          case Some(a) =>
            val qi = QuestInfoWithID(a.id, a.info)
            db.theme.readById(a.info.themeId) ifSome { theme =>
              val u = db.user.selectQuestProposalVote(user.id, qi, ThemeInfoWithID(theme.id, theme.info))
              OkApiResult(GetQuestProposalToVoteResult(OK, u.map(_.profile)))
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
      case OK =>
        // 1. get quest to vote.
        // 2. vote it.
        val reward = request.user.getQuestProposalVoteReward

        db.quest.readById(request.user.profile.questProposalVoteContext.reviewingQuest.get.id) match {
          case None =>
            InternalErrorApiResult(s"Unable to find quest with id for voting ${request.user.profile.questProposalVoteContext.reviewingQuest.get.id}")
          case Some(q) => {

            voteQuest(VoteQuestUpdateRequest(q, request.vote, request.duration, request.difficulty))

          } ifOk { r =>

            makeTask(MakeTaskRequest(request.user, taskType = Some(TaskType.VoteQuestProposals)))

          } ifOk {

            adjustAssets(AdjustAssetsRequest(user = request.user, reward = Some(reward)))

          } ifOk { r =>

            val liked = request.vote == QuestProposalVote.Cool
            val u = db.user.recordQuestProposalVote(r.user.id, q.id, liked)

            val author = if (liked) {
              db.user.readById(q.info.authorId).map(a => PublicProfileWithID(a.id, a.profile.publicProfile))
            } else {
              None
            }

            OkApiResult(VoteQuestProposalResult(OK, u.map(_.profile), Some(reward), author))
          }
        }
      case a => OkApiResult(VoteQuestProposalResult(a))
    }
  }

}


