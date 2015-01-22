package controllers.domain.app.user

import components._
import controllers.domain._
import controllers.domain.app.protocol.ProfileModificationResult._
import controllers.domain.helpers._
import logic.QuestLogic
import models.domain._

case class CreateQuestRequest(user: User, quest: QuestInfoContent, friendsToHelp: List[String] = List())
case class CreateQuestResult(allowed: ProfileModificationResult, profile: Option[Profile] = None)

case class RewardQuestAuthorRequest(quest: Quest, author: User)
case class RewardQuestAuthorResult()

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

            val questLevel = r.user.profile.publicProfile.level

            val quest = Quest(
              cultureId = culture,
              info = QuestInfo(
                authorId = r.user.id,
                level = questLevel,
                content = content,
                vip = r.user.profile.publicProfile.vip,
                solveCost = QuestLogic.costOfSolvingQuest(questLevel),
                solveRewardWon = QuestLogic.rewardForWinningQuest(questLevel, this),
                solveRewardLost = QuestLogic.rewardForLosingQuest(questLevel, this)))

            db.quest.create(quest)

            (if ((config(api.ConfigParams.DebugDisableProposalCoolDown) == "1") || r.user.profile.publicProfile.vip) {
              Some(request.user)
            } else {
              db.user.updateQuestCreationCoolDown(
                request.user.id,
                request.user.getCoolDownForQuestCreation)
            }) ifSome { u =>

              {
                addQuestIncomeToDailyResult(AddQuestIncomeToDailyResultRequest(u, quest))
              } ifOk { r =>
                addToTimeLine(AddToTimeLineRequest(
                  user = r.user,
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
  def rewardQuestAuthor(request: RewardQuestAuthorRequest): ApiResult[RewardQuestAuthorResult] = handleDbException {
    import request._

    (quest.status match {

      case QuestStatus.CheatingBanned =>
        storeProposalInDailyResult(StoreProposalInDailyResultRequest(author, request.quest, penalty = Some(author.penaltyForCheatingQuest)))
        removeQuestIncomeFromDailyResult(RemoveQuestIncomeFromDailyResultRequest(author, request.quest.id))

      case QuestStatus.IACBanned =>
        storeProposalInDailyResult(StoreProposalInDailyResultRequest(author, request.quest, penalty = Some(author.penaltyForIACQuest)))
        removeQuestIncomeFromDailyResult(RemoveQuestIncomeFromDailyResultRequest(author, request.quest.id))

      case QuestStatus.OldBanned =>
        OkApiResult(StoreProposalInDailyResultResult(author))

      case _ =>
        InternalErrorApiResult[StoreProposalInDailyResultResult]("Rewarding quest author but quest status is Unexpected")
    }) ifOk {
      OkApiResult(RewardQuestAuthorResult())
    }
  }
}

