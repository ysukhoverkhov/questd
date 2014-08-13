package controllers.domain.app.user

import play.Logger
import components._
import controllers.domain.helpers._
import controllers.domain._
import logic._
import models.domain._
import models.domain.view._
import models.domain.base._
import controllers.domain._
import controllers.domain.app.protocol.ProfileModificationResult._
import controllers.domain.app.questsolution.VoteQuestSolutionUpdateRequest

case class GetQuestSolutionToVoteRequest(user: User)
case class GetQuestSolutionToVoteResult(allowed: ProfileModificationResult, profile: Option[Profile] = None)

case class VoteQuestSolutionRequest(user: User, vote: QuestSolutionVote.Value)
case class VoteQuestSolutionResult(allowed: ProfileModificationResult, profile: Option[Profile] = None, reward: Option[Assets] = None, solver: Option[PublicProfileWithID] = None)

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
          case None => OkApiResult(GetQuestSolutionToVoteResult(OutOfContent))
          case Some(a) => {
            val qsi = QuestSolutionInfoWithID(a.id, a.info)
            val qsa = db.user.readById(a.userId).map(author => PublicProfileWithID(author.id, author.profile.publicProfile))  
            val questInfo = db.quest.readById(a.info.questId).map(qi => QuestInfoWithID(qi.id, qi.info))

            questInfo ifSome { f =>
              val u = db.user.selectQuestSolutionVote(user.id, qsi, f)
              OkApiResult(GetQuestSolutionToVoteResult(OK, u.map(_.profile)))
              // TODO: remove get here.
              val u = db.user.selectQuestSolutionVote(user.id, qsi, qsa.get, questInfo.get)
            }
          }
        }
      }
      case a => OkApiResult(GetQuestSolutionToVoteResult(a))
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

        db.solution.readById(request.user.profile.questSolutionVoteContext.reviewingQuestSolution.get.id) match {
          case None => {
            Logger.error("Unable to find quest solution with id for voting " + request.user.profile.questSolutionVoteContext.reviewingQuestSolution.get.id)
            InternalErrorApiResult()
          }
          case Some(s) => {
            {
              voteQuestSolutionUpdate(VoteQuestSolutionUpdateRequest(s, request.vote))

            } ifOk { r =>

              makeTask(MakeTaskRequest(request.user, taskType = Some(TaskType.VoteQuestSolutions)))

            } ifOk { r =>

              adjustAssets(AdjustAssetsRequest(user = r.user, reward = Some(reward)))

            } ifOk { r =>

              val u = db.user.recordQuestSolutionVote(r.user.id, s.id)

              val solver = if (request.vote == QuestSolutionVote.Cool) {
                db.user.readById(s.userId).map(a => PublicProfileWithID(a.id, a.profile.publicProfile))
              } else {
                None
              }

              OkApiResult(VoteQuestSolutionResult(OK, u.map(_.profile), Some(reward), solver))
            }
          }
        }
      }

      case a => OkApiResult(VoteQuestSolutionResult(a))
    }
  }

}


