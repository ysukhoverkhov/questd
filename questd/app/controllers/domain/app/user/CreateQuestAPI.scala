package controllers.domain.app.user

import logic.QuestLogic
import models.domain._
import controllers.domain.helpers._
import controllers.domain._
import components._
import controllers.domain.app.protocol.ProfileModificationResult._
import play.Logger

case class CreateQuestRequest(user: User, quest: QuestInfoContent, friendsToHelp: List[String] = List())
case class CreateQuestResult(allowed: ProfileModificationResult, profile: Option[Profile] = None)

case class RewardQuestProposalAuthorRequest(quest: Quest, author: User)
case class RewardQuestProposalAuthorResult()

private[domain] trait CreateQuestAPI { this: DomainAPIComponent#DomainAPI with DBAccessor =>

  /**
   * Takes currently purchased theme to make a quest with it.
   */
  def createQuest(request: CreateQuestRequest): ApiResult[CreateQuestResult] = handleDbException {

    request.user.canCreateQuest(request.quest) match {
      case OK =>

        def content = if (request.user.payedAuthor) {
          request.quest
        } else {
          request.quest
        }

        {
          makeTask(MakeTaskRequest(request.user, taskType = Some(TaskType.CreateQuest)))
        } ifOk { r =>
          r.user.demo.cultureId ifSome { culture =>

            val quest = Quest(
              cultureId = culture,
              info = QuestInfo(
                authorId = r.user.id,
                level = r.user.profile.publicProfile.level,
                content = content,
                vip = r.user.profile.publicProfile.vip,
                solveCost = QuestLogic.costOfSolvingQuest(r.user.profile.publicProfile.level)))

            db.quest.create(quest)

            (if ((config(api.ConfigParams.DebugDisableProposalCooldown) == "1") || r.user.profile.publicProfile.vip) {
              db.user.updateQuestCreationCoolDown(
                request.user.id,
                request.user.getCoolDownForQuestCreation)
            } else {
              Some(request.user)
            }) ifSome { u =>

              {
                addToTimeLine(AddToTimeLineRequest(
                  user = u,
                  reason = TimeLineReason.Created,
                  objectType = TimeLineType.Quest,
                  objectId = quest.id))
              } ifOk { r =>
                addToWatchersTimeLine(AddToWatchersTimeLineRequest(
                  user = u,
                  reason = TimeLineReason.Created,
                  objectType = TimeLineType.Quest,
                  objectId = quest.id))
              } ifOk { r =>
                OkApiResult(CreateQuestResult(OK, Some(r.user.profile)))
              }
            }
          }
        }

      case (a: ProfileModificationResult) => OkApiResult(CreateQuestResult(a))
    }
  }


  /**
   * Give quest proposal author a reward on quest status change
   */
  // TODO: move me to anoher aPi. perhaps it should be something banning related.
  // TODO: store in dail reult only banning.
  def rewardQuestProposalAuthor(request: RewardQuestProposalAuthorRequest): ApiResult[RewardQuestProposalAuthorResult] = handleDbException {
    import request._

    val r = quest.status match {
      case QuestStatus.RatingBanned =>
        OkApiResult(StoreProposalInDailyResultResult(author))

      case QuestStatus.CheatingBanned =>
        storeProposalInDailyResult(StoreProposalInDailyResultRequest(author, request.quest, penalty = Some(author.penaltyForCheatingQuest)))

      case QuestStatus.IACBanned =>
        storeProposalInDailyResult(StoreProposalInDailyResultRequest(author, request.quest, penalty = Some(author.penaltyForIACQuest)))

      case QuestStatus.OldBanned =>
        OkApiResult(StoreProposalInDailyResultResult(author))

      case _ =>
        Logger.error("Rewarding quest author but quest status is Unexpected")
        InternalErrorApiResult[StoreProposalInDailyResultResult]()
    }

    r ifOk {
      OkApiResult(RewardQuestProposalAuthorResult())
    }
  }
}

