package controllers.domain.app.user

import components._
import controllers.domain._
import controllers.domain.app.protocol.ProfileModificationResult._
import controllers.domain.app.solution.VoteSolutionRequest
import controllers.domain.helpers._
import models.domain.common.ContentVote
import models.domain.user._
import models.domain.user.friends.FriendshipStatus
import models.domain.user.profile.{Profile, TaskType}
import models.domain.user.timeline.{TimeLineReason, TimeLineType}

case class VoteSolutionByUserRequest(user: User, solutionId: String, vote: ContentVote.Value)

case class VoteSolutionByUserResult(allowed: ProfileModificationResult, profile: Option[Profile] = None)

private[domain] trait VoteSolutionAPI {
  this: DomainAPIComponent#DomainAPI with DBAccessor =>

  /**
   * Vote for a solution.
   */
  def voteSolutionByUser(request: VoteSolutionByUserRequest): ApiResult[VoteSolutionByUserResult] = handleDbException {
    import request._

    user.canVoteSolution(solutionId, vote) match {
      case OK =>

        db.solution.readById(solutionId) ifSome { s =>
          {
            val isFriend = user.friends.filter(_.status == FriendshipStatus.Accepted).map(_.friendId).contains(s.info.authorId)
            voteSolution(VoteSolutionRequest(s, isFriend, request.vote))
          } map { r =>

            if (request.vote == ContentVote.Cool)
              makeTask(MakeTaskRequest(request.user, taskType = Some(TaskType.LikeSolutions)))
            else
              OkApiResult(MakeTaskResult(request.user))

          } map { r =>
            db.user.recordSolutionVote(r.user.id, s.id, request.vote) ifSome { u =>

              (if (request.vote == ContentVote.Cool) {
                addToWatchersTimeLine(AddToWatchersTimeLineRequest(
                  user = u,
                  reason = TimeLineReason.Liked,
                  objectType = TimeLineType.Solution,
                  objectId = s.id
                )) map { r =>
                  OkApiResult(UserInternalResult(r.user))
                }
              } else {
                removeFromTimeLine(RemoveFromTimeLineRequest(user = u, objectId = s.id)) map {r =>
                  OkApiResult(UserInternalResult(r.user))
                }
              }) map { r =>
                OkApiResult(VoteSolutionByUserResult(OK, Some(r.user.profile)))
              }
            }
          }
        }

      case a => OkApiResult(VoteSolutionByUserResult(a))
    }
  }
}

