package controllers.domain.app.user

import components._
import controllers.domain.helpers._
import models.domain._
import controllers.domain._
import controllers.domain.app.protocol.ProfileModificationResult._
import controllers.domain.app.solution.VoteSolutionUpdateRequest

case class VoteSolutionRequest(user: User, solutionId: String, vote: ContentVote.Value)

case class VoteSolutionResult(allowed: ProfileModificationResult, profile: Option[Profile] = None)

private[domain] trait VoteSolutionAPI {
  this: DomainAPIComponent#DomainAPI with DBAccessor =>

  /**
   * Vote for a solution.
   */
  // TODO: rename me to voteSolutionByUser
  def voteSolution(request: VoteSolutionRequest): ApiResult[VoteSolutionResult] = handleDbException {
    import request._

    user.canVoteSolution(solutionId) match {
      case OK =>

        db.solution.readById(solutionId) ifSome { s =>
          {
            val isFriend = user.friends.filter(_.status == FriendshipStatus.Accepted).map(_.friendId).contains(s.info.authorId)
            voteSolutionUpdate(VoteSolutionUpdateRequest(s, isFriend, request.vote))
          } ifOk { r =>

            if (request.vote == ContentVote.Cool)
              makeTask(MakeTaskRequest(request.user, taskType = Some(TaskType.LikeSolutions)))
            else
              OkApiResult(MakeTaskResult(request.user))

          } ifOk { r =>
            db.user.recordSolutionVote(r.user.id, s.id, request.vote) ifSome { u =>

              (if (request.vote == ContentVote.Cool) {
                addToWatchersTimeLine(AddToWatchersTimeLineRequest(
                  user = u,
                  reason = TimeLineReason.Liked,
                  objectType = TimeLineType.Solution,
                  objectId = s.id
                )) ifOk { r =>
                  OkApiResult(UserInternalResult(r.user))
                }
              } else {
                removeFromTimeLine(RemoveFromTimeLineRequest(user = u, objectId = s.id)) ifOk {r =>
                  OkApiResult(UserInternalResult(r.user))
                }
              }) ifOk { r =>
                OkApiResult(VoteSolutionResult(OK, Some(r.user.profile)))
              }
            }
          }
        }

      case a => OkApiResult(VoteSolutionResult(a))
    }
  }
}

