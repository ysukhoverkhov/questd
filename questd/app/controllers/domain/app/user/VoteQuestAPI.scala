package controllers.domain.app.user

import components._
import controllers.domain._
import controllers.domain.app.protocol.CommonCode
import controllers.domain.app.quest._
import controllers.domain.helpers._
import models.domain.common.ContentVote
import models.domain.quest.QuestStatus
import models.domain.user._
import models.domain.user.profile.{Profile, TaskType}
import models.domain.user.timeline.{TimeLineReason, TimeLineType}
import models.view.QuestView

object VoteQuestByUserCode extends Enumeration with CommonCode {
  val QuestAlreadyVoted = Value
  val CantVoteOwnQuest = Value
}
case class VoteQuestByUserRequest(
  user: User,
  questId: String,
  vote: ContentVote.Value)
case class VoteQuestByUserResult(
  allowed: VoteQuestByUserCode.Value,
  profile: Option[Profile] = None,
  modifiedQuests: List[QuestView] = List.empty)

object HideOwnQuestCode extends Enumeration with CommonCode {
  val QuestNotFound = Value
  val NotOwnQuest = Value
  val QuestNotInRotation = Value
}
case class HideOwnQuestRequest(user: User, questId: String)
case class HideOwnQuestResult(allowed: HideOwnQuestCode.Value)

private[domain] trait VoteQuestAPI { this: DomainAPIComponent#DomainAPI with DBAccessor =>

  /**
   * Get cost of quest to shuffle.
   */
  def voteQuestByUser(request: VoteQuestByUserRequest): ApiResult[VoteQuestByUserResult] = handleDbException {
    import VoteQuestByUserCode._

    request.user.canVoteQuest(request.questId, request.vote) match {
      case OK =>
        db.quest.readById(request.questId) ifSome { q =>
          {
            voteQuest(VoteQuestRequest(q, request.vote))
          } map { voteQuestResult =>
            {
              if (request.vote == ContentVote.Cool)
                makeTask(MakeTaskRequest(request.user, taskType = Some(TaskType.LikeQuests)))
              else
                OkApiResult(MakeTaskResult(request.user))
            } map { r =>
              db.user.recordQuestVote(r.user.id, q.id, request.vote) ifSome { u =>
                (if (request.vote == ContentVote.Cool) {
                  addToWatchersTimeLine(
                    AddToWatchersTimeLineRequest(
                      user = u,
                      reason = TimeLineReason.Liked,
                      objectType = TimeLineType.Quest,
                      objectId = q.id
                    )) map { r =>
                    OkApiResult(UserInternalResult(r.user))
                  }
                } else {
                  removeFromTimeLine(RemoveFromTimeLineRequest(user = u, objectId = q.id)) map { r =>
                    OkApiResult(UserInternalResult(r.user))
                  }
                }) map { r =>
                  OkApiResult(
                    VoteQuestByUserResult(
                      allowed = OK,
                      profile = Some(r.user.profile),
                      modifiedQuests = List(QuestView(voteQuestResult.quest, r.user))))
                }
              }
            }
          }
        }

      case notAllowed => OkApiResult(VoteQuestByUserResult(notAllowed))
    }
  }

  /**
   * Hides own player's quest.
   */
  def hideOwnQuest(request: HideOwnQuestRequest): ApiResult[HideOwnQuestResult] = handleDbException {
    import HideOwnQuestCode._
    import request._

    db.quest.readById(questId).fold {
      OkApiResult(HideOwnQuestResult(QuestNotFound))
    } { quest =>
      if (quest.info.authorId != user.id) {
        OkApiResult(HideOwnQuestResult(NotOwnQuest))
      } else if (quest.status != QuestStatus.InRotation) {
        OkApiResult(HideOwnQuestResult(QuestNotInRotation))
      } else {
        db.quest.updateStatus(questId, QuestStatus.AuthorBanned)
        OkApiResult(HideOwnQuestResult(OK))
      }
    }
  }
}

