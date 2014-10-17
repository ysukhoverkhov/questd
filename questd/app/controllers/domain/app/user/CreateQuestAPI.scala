package controllers.domain.app.user

import models.domain._
import controllers.domain.helpers._
import controllers.domain._
import components._
import controllers.domain.app.protocol.ProfileModificationResult._

case class CreateQuestRequest(user: User, quest: QuestInfoContent, friendsToHelp: List[String] = List())
case class CreateQuestResult(allowed: ProfileModificationResult, profile: Option[Profile] = None)

case class RewardQuestProposalAuthorRequest(quest: Quest, author: User)
case class RewardQuestProposalAuthorResult()

private[domain] trait CreateQuestAPI { this: DomainAPIComponent#DomainAPI with DBAccessor =>

  /**
   * Takes currently purchased theme to make a quest with it.
   */
  def createQuest(request: CreateQuestRequest): ApiResult[CreateQuestResult] = handleDbException {

    request.user.canProposeQuest(request.quest.media.contentType) match {
      case OK =>

        if (request.quest.description.length > api.config(api.ConfigParams.ProposalMaxDescriptionLength).toInt) {
          OkApiResult(CreateQuestResult(LimitExceeded, None))
        } else {

          def content = if (request.user.payedAuthor) {
            request.quest
          } else {
            request.quest
          }

          {
            makeTask(MakeTaskRequest(request.user, taskType = Some(TaskType.SubmitQuestProposal)))
          } ifOk { r =>
              r.user.demo.cultureId ifSome { culture =>

                val quest = Quest(
                  cultureId = culture,
                  info = QuestInfo(
                    authorId = r.user.id,
                    content = content,
                    vip = r.user.profile.publicProfile.vip))

                db.quest.create(quest)

                db.user.resetQuestProposal(
                  request.user.id,
                  config(api.ConfigParams.DebugDisableProposalCooldown) == "1",
                  request.user.getCooldownForTakeTheme) ifSome { u =>

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
        }

      case (a: ProfileModificationResult) => OkApiResult(CreateQuestResult(a))
    }
  }

  /**
   * Give up quest proposal for the user if he is going to make one.
   */
//  def giveUpQuestProposal(request: GiveUpQuestProposalRequest): ApiResult[GiveUpQuestProposalResult] = handleDbException {
//    import request._
//
//    user.canGiveUpQuestProposal match {
//      case OK =>
//
//        adjustAssets(AdjustAssetsRequest(user = user, cost = Some(user.costOfGivingUpQuestProposal))) ifOk { r =>
//          val u = db.user.resetQuestProposal(
//            r.user.id,
//            config(api.ConfigParams.DebugDisableProposalCooldown) == "1")
//          OkApiResult(GiveUpQuestProposalResult(OK, u.map(_.profile)))
//        }
//
//      case (a: ProfileModificationResult) => OkApiResult(GiveUpQuestProposalResult(a))
//    }
//  }

  /**
   * Get cost for giving up quest proposal.
   */
//  def getQuestProposalGiveUpCost(request: GetQuestProposalGiveUpCostRequest): ApiResult[GetQuestProposalGiveUpCostResult] = handleDbException {
//    import request._
//
//    OkApiResult(GetQuestProposalGiveUpCostResult(OK, Some(user.costOfGivingUpQuestProposal)))
//  }

  /**
   * Get cost for asking a friend to help us.
   */
//  def getQuestProposalHelpCost(request: GetQuestProposalHelpCostRequest): ApiResult[GetQuestProposalHelpCostResult] = handleDbException {
//    import request._
//
//    OkApiResult(GetQuestProposalHelpCostResult(OK, Some(user.costOfAskingForHelpWithProposal)))
//  }

  /**
   * Give quest proposal author a reward on quest status change
   */
  def rewardQuestProposalAuthor(request: RewardQuestProposalAuthorRequest): ApiResult[RewardQuestProposalAuthorResult] = handleDbException {
    import request._

    val r = quest.status match {
//      case QuestStatus.InRotation =>
//        storeProposalInDailyResult(StoreProposalInDailyResultRequest(author, request.quest, reward = Some(quest.approveReward)))

      case QuestStatus.RatingBanned =>
        OkApiResult(StoreProposalInDailyResultResult(author))

      case QuestStatus.CheatingBanned =>
        storeProposalInDailyResult(StoreProposalInDailyResultRequest(author, request.quest, penalty = Some(author.penaltyForCheatingQuest)))

      case QuestStatus.IACBanned =>
        storeProposalInDailyResult(StoreProposalInDailyResultRequest(author, request.quest, penalty = Some(author.penaltyForIACQuest)))

      case QuestStatus.OldBanned =>
        OkApiResult(StoreProposalInDailyResultResult(author))
    }

    r ifOk {
      OkApiResult(RewardQuestProposalAuthorResult())
    }
  }
}

