package controllers.domain.app.user

import components._
import controllers.domain._
import controllers.domain.app.protocol.ProfileModificationResult._
import controllers.domain.app.solution.{VoteSolutionResult, VoteSolutionRequest}
import controllers.domain.helpers._
import models.domain.common.ContentVote
import models.domain.user._
import models.domain.user.friends.FriendshipStatus
import models.domain.user.profile.{Profile, TaskType}
import models.domain.user.timeline.{TimeLineReason, TimeLineType}
import models.view.SolutionView

case class VoteSolutionByUserRequest(user: User, solutionId: String, vote: ContentVote.Value)
case class VoteSolutionByUserResult(
  allowed: ProfileModificationResult,
  profile: Option[Profile] = None,
  modifiedSolutions: List[SolutionView] = List.empty)

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
          } map { voteResult => voteResult match {
            case VoteSolutionResult(votedSolution) => {
              if (request.vote == ContentVote.Cool)
                makeTask(MakeTaskRequest(request.user, taskType = Some(TaskType.LikeSolutions)))
              else
                OkApiResult(MakeTaskResult(request.user))
            } map { r =>
              db.user.recordSolutionVote(r.user.id, votedSolution.id, request.vote) ifSome { u =>

                (if (request.vote == ContentVote.Cool) {
                  addToWatchersTimeLine(
                    AddToWatchersTimeLineRequest(
                      user = u,
                      reason = TimeLineReason.Liked,
                      objectType = TimeLineType.Solution,
                      objectId = votedSolution.id
                    )) map { r =>
                    OkApiResult(UserInternalResult(r.user))
                  }
                } else {
                  removeFromTimeLine(RemoveFromTimeLineRequest(user = u, objectId = votedSolution.id)) map { r =>
                    OkApiResult(UserInternalResult(r.user))
                  }
                }) map { r =>
                  OkApiResult(
                    VoteSolutionByUserResult(
                      allowed = OK,
                      profile = Some(r.user.profile),
                      modifiedSolutions = List(SolutionView(votedSolution, r.user))))
                }
              }
            }
          }}
        }

      case a => OkApiResult(VoteSolutionByUserResult(a))
    }
  }
}

