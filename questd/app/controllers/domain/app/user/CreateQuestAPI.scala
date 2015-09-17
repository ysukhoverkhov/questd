package controllers.domain.app.user

import components._
import controllers.domain._
import controllers.domain.app.protocol.ProfileModificationResult._
import controllers.domain.helpers._
import logic.QuestLogic
import models.domain.quest.{Quest, QuestInfo, QuestInfoContent, QuestStatus}
import models.domain.user._
import models.domain.user.profile.{Profile, TaskType}
import models.domain.user.timeline.{TimeLineReason, TimeLineType}

case class CreateQuestRequest(user: User, quest: QuestInfoContent, friendsToHelp: List[String] = List.empty)
case class CreateQuestResult(
  allowed: ProfileModificationResult,
  profile: Option[Profile] = None)

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
        require(u.demo.cultureId.isDefined)

        val culture = u.demo.cultureId.get
        val level = u.calculateQuestLevel

        val quest = Quest(
          cultureId = culture,
          info = QuestInfo(
            authorId = u.id,
            level = level,
            content = request.quest,
            vip = u.profile.publicProfile.vip,
            solveCost = QuestLogic.costOfSolvingQuest(level),
            solveReward = QuestLogic.rewardForSolvingQuest(level, this),
            victoryReward = QuestLogic.rewardForWinningBattle(level, this),
            defeatReward = QuestLogic.rewardForLosingBattle(level, this)
          ))

        db.quest.create(quest)

        // making all db calls
        runWhileSome(u)(
        { u: User =>
          if ((config(api.DefaultConfigParams.DebugDisableQuestCreationCoolDown) == "1") || u.profile.publicProfile.vip) {
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
          } map { r =>
            addToTimeLine(AddToTimeLineRequest(
              user = r.user,
              reason = TimeLineReason.Created,
              objectType = TimeLineType.Quest,
              objectId = quest.id))
          } map { r =>
            addToWatchersTimeLine(AddToWatchersTimeLineRequest(
              user = r.user,
              reason = TimeLineReason.Created,
              objectType = TimeLineType.Quest,
              objectId = quest.id))
          } map { r =>
            OkApiResult(CreateQuestResult(
              allowed = OK,
              profile = Some(r.user.profile)))
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
        storeQuestInDailyResult(StoreQuestInDailyResultRequest(
          user = author,
          quest = request.quest,
          reward = -author.penaltyForCheatingQuest))
        removeQuestIncomeFromDailyResult(RemoveQuestIncomeFromDailyResultRequest(author, request.quest.id))
        removeFromTimeLine(RemoveFromTimeLineRequest(author, request.quest.id))

      case QuestStatus.IACBanned =>
        storeQuestInDailyResult(StoreQuestInDailyResultRequest(
          user = author,
          quest = request.quest,
          reward = -author.penaltyForIACQuest))
        removeQuestIncomeFromDailyResult(RemoveQuestIncomeFromDailyResultRequest(author, request.quest.id))
        removeFromTimeLine(RemoveFromTimeLineRequest(author, request.quest.id))

      case QuestStatus.OldBanned =>
        // We do nothing here.
        OkApiResult(RewardQuestAuthorResult())

      case _ =>
        InternalErrorApiResult[StoreQuestInDailyResultResult]("Rewarding quest author but quest status is Unexpected")
    }) map {
      OkApiResult(RewardQuestAuthorResult())
    }
  }
}

