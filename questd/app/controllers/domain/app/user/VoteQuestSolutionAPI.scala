package controllers.domain.app.user

import play.Logger
import components._
import controllers.domain.helpers._
import models.domain._
import models.domain.view._
import controllers.domain._
import controllers.domain.app.protocol.ProfileModificationResult._
import controllers.domain.app.questsolution.VoteQuestSolutionUpdateRequest

case class GetQuestSolutionToVoteRequest(user: User)

case class GetQuestSolutionToVoteResult(allowed: ProfileModificationResult, profile: Option[Profile] = None)

case class VoteQuestSolutionRequest(user: User, vote: QuestSolutionVote.Value)

case class VoteQuestSolutionResult(allowed: ProfileModificationResult, profile: Option[Profile] = None, reward: Option[Assets] = None, solver: Option[PublicProfileWithID] = None)

private[domain] trait VoteQuestSolutionAPI {
  this: DomainAPIComponent#DomainAPI with DBAccessor =>

  /**
   * Get quest solution to vote for.
   */
  def getQuestSolutionToVote(request: GetQuestSolutionToVoteRequest): ApiResult[GetQuestSolutionToVoteResult] = handleDbException {
    import request._

    user.canGetQuestSolutionForVote match {
      case OK =>

        user.getQuestSolutionToVote match {
          case None =>
            OkApiResult(GetQuestSolutionToVoteResult(OutOfContent))
          case Some(solution) =>
            val qsInfo = QuestSolutionInfoWithID(solution.id, solution.info)
            val qsAuthor = db.user.readById(solution.info.authorId).map(author => PublicProfileWithID(author.id, author.profile.publicProfile))
            val questInfo = db.quest.readById(solution.info.questId).map(qi => QuestInfoWithID(qi.id, qi.info))
            questInfo ifSome { questInfoValue =>
              qsAuthor ifSome { qsaValue =>
                db.user.selectQuestSolutionVote(user.id, qsInfo, qsaValue, questInfoValue) ifSome { u =>

                  if (u.mustVoteSolutions.contains(solution)) {
                    db.user.removeMustVoteSolution(u.id, solution.id)
                  }

                  OkApiResult(GetQuestSolutionToVoteResult(OK, Some(u.profile)))
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
      case OK =>
        // 1. get quest to vote.
        // 2. update quest params.
        // 3. check change quest state
        // 4. save quest in db.
        val reward = request.user.getQuestSolutionVoteReward

        request.user.profile.questSolutionVoteContext.reviewingQuestSolution ifSome { qs =>
          db.solution.readById(qs.id) ifSome { s => {
            voteQuestSolutionUpdate(VoteQuestSolutionUpdateRequest(s, request.vote))

          } ifOk { r =>

            makeTask(MakeTaskRequest(request.user, taskType = Some(TaskType.VoteQuestSolutions)))

          } ifOk { r =>

            adjustAssets(AdjustAssetsRequest(user = r.user, reward = Some(reward)))

          } ifOk { r =>

            val u = db.user.recordQuestSolutionVote(r.user.id, s.id)

            val solver = if (request.vote == QuestSolutionVote.Cool) {
              db.user.readById(s.info.authorId).map(a => PublicProfileWithID(a.id, a.profile.publicProfile))
            } else {
              None
            }

            OkApiResult(VoteQuestSolutionResult(OK, u.map(_.profile), Some(reward), solver))
          }
          }
        }

      case a => OkApiResult(VoteQuestSolutionResult(a))
    }
  }

}


