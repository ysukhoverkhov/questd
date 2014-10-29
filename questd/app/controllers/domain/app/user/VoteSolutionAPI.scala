package controllers.domain.app.user

import components._
import controllers.domain.helpers._
import models.domain._
import controllers.domain._
import controllers.domain.app.protocol.ProfileModificationResult._
import controllers.domain.app.questsolution.VoteQuestSolutionUpdateRequest

case class VoteSolutionRequest(user: User, solutionId: String, vote: ContentVote.Value)

case class VoteSolutionResult(allowed: ProfileModificationResult, profile: Option[Profile] = None)

private[domain] trait VoteSolutionAPI {
  this: DomainAPIComponent#DomainAPI with DBAccessor =>

  /**
   * Vote for a solution.
   */// TODO: remane me to voteSolution after testing
  def voteSolution(request: VoteSolutionRequest): ApiResult[VoteSolutionResult] = handleDbException {
    import request._

    // TODO: tests:
    // 1. this function works in general

    user.canVoteSolution(solutionId) match {
      case OK =>

        db.solution.readById(solutionId) ifSome { qs =>
          db.solution.readById(qs.id) ifSome { s =>
            {
              voteQuestSolutionUpdate(VoteQuestSolutionUpdateRequest(s, request.vote))

            } ifOk { r =>

              makeTask(MakeTaskRequest(request.user, taskType = Some(TaskType.VoteQuestSolutions)))

            } ifOk { r =>
              db.user.recordTimeLineVote(r.user.id, s.id, request.vote) ifSome { u =>

                (if (request.vote == ContentVote.Cool) {
                  addToWatchersTimeLine(AddToWatchersTimeLineRequest(
                    user = u,
                    reason = TimeLineReason.Liked,
                    objectType = TimeLineType.Quest,
                    objectId = s.id,
                    objectAuthorId = Some(s.info.authorId)
                  ))
                } else {
                  OkApiResult(AddToWatchersTimeLineResult(u))
                }) ifOk { r =>
                  OkApiResult(VoteSolutionResult(OK, Some(r.user.profile)))
                }
              }
            }
          }
        }

      case a => OkApiResult(VoteSolutionResult(a))
    }
  }
}

