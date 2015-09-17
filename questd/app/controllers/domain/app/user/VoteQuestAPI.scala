package controllers.domain.app.user

import components._
import controllers.domain._
import controllers.domain.app.protocol.ProfileModificationResult._
import controllers.domain.app.quest._
import controllers.domain.helpers._
import models.domain.common.ContentVote
import models.domain.user._
import models.domain.user.profile.{Profile, TaskType}
import models.domain.user.timeline.{TimeLineReason, TimeLineType}
import models.view.QuestView

case class VoteQuestByUserRequest(
  user: User,
  questId: String,
  vote: ContentVote.Value)
case class VoteQuestByUserResult(
  allowed: ProfileModificationResult,
  profile: Option[Profile] = None,
  quest: Option[QuestView] = None)

private[domain] trait VoteQuestAPI { this: DomainAPIComponent#DomainAPI with DBAccessor =>

  /**
   * Get cost of quest to shuffle.
   */
  def voteQuestByUser(request: VoteQuestByUserRequest): ApiResult[VoteQuestByUserResult] = handleDbException {

    request.user.canVoteQuest(request.questId, request.vote) match {
      case OK =>
        db.quest.readById(request.questId) ifSome { q =>
          {
            voteQuest(VoteQuestRequest(q, request.vote))
          } map { r =>
            if (request.vote == ContentVote.Cool)
              makeTask(MakeTaskRequest(request.user, taskType = Some(TaskType.LikeQuests)))
            else
              OkApiResult(MakeTaskResult(request.user))
          } map { r =>
            db.user.recordQuestVote(r.user.id, q.id, request.vote) ifSome { u =>

              (if (request.vote == ContentVote.Cool) {
                addToWatchersTimeLine(AddToWatchersTimeLineRequest(
                  user = u,
                  reason = TimeLineReason.Liked,
                  objectType = TimeLineType.Quest,
                  objectId = q.id
                )) map {r =>
                  OkApiResult(UserInternalResult(r.user))}
              } else {
                removeFromTimeLine(RemoveFromTimeLineRequest(user = u, objectId = q.id)) map {r =>
                  OkApiResult(UserInternalResult(r.user))}
              }) map { r =>
                OkApiResult(VoteQuestByUserResult(
                  allowed = OK,
                  profile = Some(r.user.profile),
                  quest = Some(QuestView(q, r.user))))
              }
            }
          }
        }

      case notAllowed => OkApiResult(VoteQuestByUserResult(notAllowed))
    }
  }
}

