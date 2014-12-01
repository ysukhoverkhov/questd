package controllers.domain.app.user

import components._
import controllers.domain.app.quest._
import controllers.domain.helpers._
import models.domain._
import controllers.domain.app.protocol.ProfileModificationResult._
import controllers.domain._

case class VoteQuestByUserRequest(
  user: User,
  questId: String,
  vote: ContentVote.Value)
case class VoteQuestByUserResult(
  allowed: ProfileModificationResult,
  profile: Option[Profile] = None)

private[domain] trait VoteQuestAPI { this: DomainAPIComponent#DomainAPI with DBAccessor =>

  /**
   * Get cost of quest to shuffle.
   */
  def voteQuestByUser(request: VoteQuestByUserRequest): ApiResult[VoteQuestByUserResult] = handleDbException {

    request.user.canVoteQuest(request.questId) match {
      case OK =>

        db.quest.readById(request.questId) ifSome { q =>
          {
            voteQuest(VoteQuestRequest(q, request.vote))
          } ifOk { r =>
            makeTask(MakeTaskRequest(request.user, taskType = Some(TaskType.VoteQuests)))
          } ifOk { r =>
            db.user.recordTimeLineVote(r.user.id, q.id, request.vote) ifSome { u =>

              (if (request.vote == ContentVote.Cool) {
                addToWatchersTimeLine(AddToWatchersTimeLineRequest(
                  user = u,
                  reason = TimeLineReason.Liked,
                  objectType = TimeLineType.Quest,
                  objectId = q.id,
                  objectAuthorId = Some(q.info.authorId)
                ))
              } else {
                OkApiResult(AddToWatchersTimeLineResult(u))
              }) ifOk { r =>
                OkApiResult(VoteQuestByUserResult(OK, Some(r.user.profile)))
              }
            }
          }
        }

      case notAllowed => OkApiResult(VoteQuestByUserResult(notAllowed))
    }
  }

}


