package controllers.domain.app.user

import components._
import controllers.domain.helpers._
import models.domain._
import controllers.domain._
import controllers.domain.app.protocol.ProfileModificationResult._
import controllers.domain.app.questsolution.VoteSolutionUpdateRequest

case class VoteSolutionRequest(user: User, solutionId: String, vote: ContentVote.Value)

case class VoteSolutionResult(allowed: ProfileModificationResult, profile: Option[Profile] = None)

private[domain] trait VoteSolutionAPI {
  this: DomainAPIComponent#DomainAPI with DBAccessor =>

  /**
   * Vote for a solution.
   */
  def voteSolution(request: VoteSolutionRequest): ApiResult[VoteSolutionResult] = handleDbException {
    import request._

    user.canVoteSolution(solutionId) match {
      case OK =>

        db.solution.readById(solutionId) ifSome { s =>
          {
            val isFriend = user.friends.filter(_.status == FriendshipStatus.Accepted).map(_.friendId).contains(s.info.authorId)
            voteQuestSolutionUpdate(VoteSolutionUpdateRequest(s, isFriend, request.vote))
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

      case a => OkApiResult(VoteSolutionResult(a))
    }
  }
}

