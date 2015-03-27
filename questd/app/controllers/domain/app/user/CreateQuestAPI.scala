package controllers.domain.app.user

import components._
import controllers.domain._
import controllers.domain.app.protocol.ProfileModificationResult._
import controllers.domain.helpers._
import logic.QuestLogic
import models.domain._

case class CreateQuestRequest(user: User, quest: QuestInfoContent, friendsToHelp: List[String] = List.empty)
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
        import request.{user => u}

        // creating quest
        require(u.demo.cultureId != None)

        val culture = u.demo.cultureId.get
        val level = u.profile.publicProfile.level

        val quest = Quest(
          cultureId = culture,
          info = QuestInfo(
            authorId = u.id,
            level = level,
            content = request.quest,
            vip = u.profile.publicProfile.vip,
            solveCost = QuestLogic.costOfSolvingQuest(level),
            solveRewardWon = QuestLogic.rewardForWinningQuest(level, this),
            solveRewardLost = QuestLogic.rewardForLosingQuest(level, this)))

        db.quest.create(quest)

        // making all db calls
        runWhileSome(u)(
        { u: User =>
          if ((config(api.ConfigParams.DebugDisableProposalCoolDown) == "1") || u.profile.publicProfile.vip) {
            Some(u)
          } else {
            db.user.updateQuestCreationCoolDown(
              u.id,
              request.user.getCoolDownForQuestCreation)
          }
        }, { u: User =>
          db.user.recordQuestCreation(
            u.id,
            quest.id)
        }) ifSome { u =>

          // Making all api calls
          {
            makeTask(MakeTaskRequest(u, taskType = Some(TaskType.CreateQuest)))
          } ifOk { r =>
            addQuestIncomeToDailyResult(AddQuestIncomeToDailyResultRequest(r.user, quest))
          } ifOk { r =>
            addToTimeLine(AddToTimeLineRequest(
              user = r.user,
              reason = TimeLineReason.Created,
              objectType = TimeLineType.Quest,
              objectId = quest.id))
          } ifOk { r =>
            addToWatchersTimeLine(AddToWatchersTimeLineRequest(
              user = r.user,
              reason = TimeLineReason.Created,
              objectType = TimeLineType.Quest,
              objectId = quest.id))
          } ifOk { r =>
            OkApiResult(CreateQuestResult(OK, Some(r.user.profile)))
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
        removeFromTimeLine(RemoveFromTimeLineRequest(author, request.quest.id))

      case QuestStatus.IACBanned =>
        storeProposalInDailyResult(StoreProposalInDailyResultRequest(author, request.quest, penalty = Some(author.penaltyForIACQuest)))
        removeQuestIncomeFromDailyResult(RemoveQuestIncomeFromDailyResultRequest(author, request.quest.id))
        removeFromTimeLine(RemoveFromTimeLineRequest(author, request.quest.id))

      case QuestStatus.OldBanned =>
        OkApiResult(StoreProposalInDailyResultResult(author))

      case _ =>
        InternalErrorApiResult[StoreProposalInDailyResultResult]("Rewarding quest author but quest status is Unexpected")
    }) ifOk {
      OkApiResult(RewardQuestAuthorResult())
    }
  }
}

