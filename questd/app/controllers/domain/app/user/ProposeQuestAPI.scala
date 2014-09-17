package controllers.domain.app.user

import models.domain._
import models.domain.view._
import play.Logger
import controllers.domain.helpers._
import controllers.domain._
import components._
import controllers.domain.app.protocol.ProfileModificationResult._

case class GetQuestThemeCostRequest(user: User)
case class GetQuestThemeCostResult(allowed: ProfileModificationResult, cost: Option[Assets] = None)

case class PurchaseQuestThemeRequest(user: User)
case class PurchaseQuestThemeResult(allowed: ProfileModificationResult, profile: Option[Profile] = None)

case class GetQuestThemeTakeCostRequest(user: User)
case class GetQuestThemeTakeCostResult(allowed: ProfileModificationResult, cost: Option[Assets] = None)

case class TakeQuestThemeRequest(user: User)
case class TakeQuestThemeResult(allowed: ProfileModificationResult, profile: Option[Profile] = None)

case class ProposeQuestRequest(user: User, quest: QuestInfoContent, friendsToHelp: List[String] = List())
case class ProposeQuestResult(allowed: ProfileModificationResult, profile: Option[Profile] = None)

case class GiveUpQuestProposalRequest(user: User)
case class GiveUpQuestProposalResult(allowed: ProfileModificationResult, profile: Option[Profile] = None)

case class DeadlineQuestProposalRequest(user: User)
case class DeadlineQuestProposalResult(user: Option[User])

case class GetQuestProposalGiveUpCostRequest(user: User)
case class GetQuestProposalGiveUpCostResult(allowed: ProfileModificationResult, cost: Option[Assets] = None)

case class GetQuestProposalHelpCostRequest(user: User)
case class GetQuestProposalHelpCostResult(allowed: ProfileModificationResult, cost: Option[Assets] = None)

case class RewardQuestProposalAuthorRequest(quest: Quest, author: User)
case class RewardQuestProposalAuthorResult()

private[domain] trait ProposeQuestAPI { this: DomainAPIComponent#DomainAPI with DBAccessor =>

  /**
   * Get cost of next quest purchase.
   */
  def getQuestThemeCost(request: GetQuestThemeCostRequest): ApiResult[GetQuestThemeCostResult] = handleDbException {
    import request._

    OkApiResult(GetQuestThemeCostResult(OK, Some(user.costOfPurchasingQuestProposal)))
  }

  /**
   * Purchase quest theme. Check for all conditions are meat.
   * Returns purchased quest theme.
   */
  def purchaseQuestTheme(request: PurchaseQuestThemeRequest): ApiResult[PurchaseQuestThemeResult] = handleDbException {

    val user = ensureNoDeadlineProposal(request.user)

    user.canPurchaseQuestProposals match {
      case OK =>

        val themeCost = user.costOfPurchasingQuestProposal

        adjustAssets(AdjustAssetsRequest(user = user, cost = Some(themeCost))) ifOk { r =>
          val reward = r.user.rewardForMakingApprovedQuest
          val themesCount = db.theme.count

          // Recursion for reseting today selected themes.
          def selectRandomThemeToPresentUser(user: User): ApiResult[PurchaseQuestThemeResult] = {
            user.getRandomThemeForQuestProposal(themesCount) match {
              case Some(t) =>
                val sampleQuest = {
                  val all = db.quest.allWithStatusAndThemeByPoints(QuestStatus.InRotation.toString, t.id)
                  if (all.hasNext) {
                    Some(all.next().info)
                  } else {
                    None
                  }
                }

                db.user.purchaseQuestTheme(user.id, ThemeWithID(t.id, t.info), sampleQuest, reward) ifSome { v =>
                  OkApiResult(PurchaseQuestThemeResult(OK, Some(v.profile)))
                }

              case None =>
                if (user.profile.questProposalContext.todayReviewedThemeIds.size == 0) {
                  OkApiResult(PurchaseQuestThemeResult(OutOfContent))
                } else {

                  db.user.resetTodayReviewedThemes(user.id) ifSome { v =>
                    selectRandomThemeToPresentUser(v)
                  }
                }

            }
          }

          selectRandomThemeToPresentUser(r.user)
        }
      case a => OkApiResult(PurchaseQuestThemeResult(a))
    }
  }

  private def ensureNoDeadlineProposal(user: User): User = {
    if (user.proposalDeadlineReached) {
      deadlineQuestProposal(DeadlineQuestProposalRequest(user)).body.get.user.get
    } else {
      user
    }
  }

  /**
   * Stop from solving quests because its deadline reached.
   */
  def deadlineQuestProposal(request: DeadlineQuestProposalRequest): ApiResult[DeadlineQuestProposalResult] = handleDbException {
    storeProposalOutOfTimePenalty(StoreProposalOutOfTimePenaltyReqest(request.user, request.user.costOfGivingUpQuestProposal)) ifOk { r =>
      val u = db.user.resetQuestProposal(
        r.user.id,
        config(api.ConfigParams.DebugDisableProposalCooldown) == "1")
      OkApiResult(DeadlineQuestProposalResult(u))
    }
  }

  /**
   * Get cost of proposing quest.
   */
  def getQuestThemeTakeCost(request: GetQuestThemeTakeCostRequest): ApiResult[GetQuestThemeTakeCostResult] = handleDbException {
    import request._

    OkApiResult(GetQuestThemeTakeCostResult(OK, Some(user.costOfTakingQuestTheme)))
  }

  /**
   * Takes currently purchased theme to make a quest with it.
   */
  def takeQuestTheme(request: TakeQuestThemeRequest): ApiResult[TakeQuestThemeResult] = handleDbException {

    request.user.canTakeQuestTheme match {

      case OK => {
        adjustAssets(AdjustAssetsRequest(user = request.user, cost = Some(request.user.costOfTakingQuestTheme)))
      } ifOk { r =>

        r.user.profile.questProposalContext.purchasedTheme ifSome { v =>

          val u = db.user.takeQuestTheme(r.user.id, v, r.user.getCooldownForTakeTheme)
          db.theme.updateLastUseDate(v.id)
          OkApiResult(TakeQuestThemeResult(OK, u.map(_.profile)))
        }
      }

      case (a: ProfileModificationResult) => OkApiResult(TakeQuestThemeResult(a))
    }
  }

  /**
   * Takes currently purchased theme to make a quest with it.
   */
  def proposeQuest(request: ProposeQuestRequest): ApiResult[ProposeQuestResult] = handleDbException {

    val user = ensureNoDeadlineProposal(request.user)

    user.canProposeQuest(request.quest.media.contentType) match {
      case OK =>

        if (request.quest.description.length > 140) { // TODO: move it to config.
          OkApiResult(ProposeQuestResult(LimitExceeded, None))
        } else {
          def content = if (request.user.payedAuthor) {
            request.quest
          } else {
            request.quest
          }

          {

            makeTask(MakeTaskRequest(user, taskType = Some(TaskType.SubmitQuestProposal)))

          } ifOk { r =>

            r.user.profile.questProposalContext.takenTheme ifSome { takenTheme =>
              r.user.demo.cultureId ifSome { culture =>
                db.quest.create(
                  Quest(
                    cultureId = culture,
                    approveReward = r.user.profile.questProposalContext.approveReward,
                    info = QuestInfo(
                      authorId = r.user.id,
                      themeId = takenTheme.id,
                      content = content,

                      vip = r.user.profile.publicProfile.vip)))

                val u = db.user.resetQuestProposal(
                  user.id,
                  config(api.ConfigParams.DebugDisableProposalCooldown) == "1")

                OkApiResult(ProposeQuestResult(OK, u.map(_.profile)))

              }
            }
          }
        }

      case (a: ProfileModificationResult) => OkApiResult(ProposeQuestResult(a))
    }
  }

  /**
   * Give up quest proposal for the user if he is going to make one.
   */
  def giveUpQuestProposal(request: GiveUpQuestProposalRequest): ApiResult[GiveUpQuestProposalResult] = handleDbException {
    import request._

    user.canGiveUpQuestProposal match {
      case OK =>

        adjustAssets(AdjustAssetsRequest(user = user, cost = Some(user.costOfGivingUpQuestProposal))) ifOk { r =>
          val u = db.user.resetQuestProposal(
            r.user.id,
            config(api.ConfigParams.DebugDisableProposalCooldown) == "1")
          OkApiResult(GiveUpQuestProposalResult(OK, u.map(_.profile)))
        }

      case (a: ProfileModificationResult) => OkApiResult(GiveUpQuestProposalResult(a))
    }
  }

  /**
   * Get cost for giving up quest proposal.
   */
  def getQuestProposalGiveUpCost(request: GetQuestProposalGiveUpCostRequest): ApiResult[GetQuestProposalGiveUpCostResult] = handleDbException {
    import request._

    OkApiResult(GetQuestProposalGiveUpCostResult(OK, Some(user.costOfGivingUpQuestProposal)))
  }

  /**
   * Get cost for asking a friend to help us.
   */
  def getQuestProposalHelpCost(request: GetQuestProposalHelpCostRequest): ApiResult[GetQuestProposalHelpCostResult] = handleDbException {
    import request._

    OkApiResult(GetQuestProposalHelpCostResult(OK, Some(user.costOfAskingForHelpWithProposal)))
  }

  /**
   * Give quest proposal author a reward on quest status change
   */
  def rewardQuestProposalAuthor(request: RewardQuestProposalAuthorRequest): ApiResult[RewardQuestProposalAuthorResult] = handleDbException {
    import request._

    val r = quest.status match {
      case QuestStatus.OnVoting =>
        Logger.error("We are rewarding player for proposal what is on voting.")
        InternalErrorApiResult()
      case QuestStatus.InRotation =>
        storeProposalInDailyResult(StoreProposalInDailyResultRequest(author, request.quest, reward = Some(quest.approveReward)))

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

