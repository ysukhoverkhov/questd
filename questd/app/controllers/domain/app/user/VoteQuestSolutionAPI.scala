package controllers.domain.app.user

import components._
import controllers.domain.helpers._
import models.domain._
import controllers.domain._
import controllers.domain.app.protocol.ProfileModificationResult._
import controllers.domain.app.questsolution.VoteQuestSolutionUpdateRequest

case class VoteQuestSolutionRequest(user: User, solutionId: String, vote: ContentVote.Value)

case class VoteQuestSolutionResult(allowed: ProfileModificationResult, profile: Option[Profile] = None)

private[domain] trait VoteQuestSolutionAPI {
  this: DomainAPIComponent#DomainAPI with DBAccessor =>

  /**
   * Get quest solution to vote for.
   */
  // TODO: remove me.
//  def getQuestSolutionToVote(request: GetQuestSolutionToVoteRequest): ApiResult[GetQuestSolutionToVoteResult] = handleDbException {
//    import request._
//
//    user.canGetQuestSolutionForVote match {
//      case OK =>
//
//        user.getQuestSolutionForTimeLine match {
//          case None =>
//            OkApiResult(GetQuestSolutionToVoteResult(OutOfContent))
//          case Some(solution) =>
//            val qsInfo = QuestSolutionInfoWithID(solution.id, solution.info)
//            val qsAuthor = db.user.readById(solution.info.authorId).map(author => PublicProfileWithID(author.id, author.profile.publicProfile))
//            val questInfo = db.quest.readById(solution.info.questId).map(qi => QuestInfoWithID(qi.id, qi.info))
//            questInfo ifSome { questInfoValue =>
//              qsAuthor ifSome { qsaValue =>
//                db.user.selectQuestSolutionVote(user.id, qsInfo, qsaValue, questInfoValue) ifSome { u =>
//
//                  if (u.mustVoteSolutions.contains(solution)) {
//                    db.user.removeMustVoteSolution(u.id, solution.id)
//                  }
//
//                  OkApiResult(GetQuestSolutionToVoteResult(OK, Some(u.profile)))
//                }
//              }
//            }
//        }
//      case a => OkApiResult(GetQuestSolutionToVoteResult(a))
//    }
//  }

  /**
   * Vote for a solution.
   */
  def voteQuestSolution(request: VoteQuestSolutionRequest): ApiResult[VoteQuestSolutionResult] = handleDbException {
    import request._

    // TODO: tests:
    // 1. this function works in general

    user.canVoteSolution(solutionId) match {
      case OK =>
        // 1. get quest to vote.
        // 2. update quest params.
        // 3. check change quest state
        // 4. save quest in db.



        db.solution.readById(solutionId) ifSome { qs =>
          db.solution.readById(qs.id) ifSome { s => {
            voteQuestSolutionUpdate(VoteQuestSolutionUpdateRequest(s, request.vote))

          } ifOk { r =>

            makeTask(MakeTaskRequest(request.user, taskType = Some(TaskType.VoteQuestSolutions)))

          } ifOk { r =>

            // TODO: check wht it's doing
            val u = db.user.recordQuestSolutionVote(r.user.id, s.id)

            // TODO: add to friend's time line.
            // TODO: check liked quests are added to friend's time line.

            OkApiResult(VoteQuestSolutionResult(OK, u.map(_.profile)))
          }
          }
        }

      case a => OkApiResult(VoteQuestSolutionResult(a))
    }
  }

}


