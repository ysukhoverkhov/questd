package controllers.domain.user

import models.domain._
import models.domain.base._
import models.store._
import play.Logger
import helpers._
import controllers.domain.helpers.exceptionwrappers._
import controllers.domain._
import components._
import logic._
import protocol.ProfileModificationResult._

case class GetQuestThemeCostRequest(user: User)
case class GetQuestThemeCostResult(allowed: ProfileModificationResult, cost: Assets)

case class PurchaseQuestThemeRequest(user: User)
case class PurchaseQuestThemeResult(allowed: ProfileModificationResult, profile: Option[Profile] = None)

case class GetQuestThemeTakeCostRequest(user: User)
case class GetQuestThemeTakeCostResult(allowed: ProfileModificationResult, cost: Assets)

case class TakeQuestThemeRequest(user: User)
case class TakeQuestThemeResult(allowed: ProfileModificationResult, profile: Option[Profile] = None)

case class ProposeQuestRequest(user: User, quest: QuestInfoContent)
case class ProposeQuestResult(allowed: ProfileModificationResult, profile: Option[Profile] = None)

case class GiveUpQuestProposalRequest(user: User)
case class GiveUpQuestProposalResult(allowed: ProfileModificationResult, profile: Option[Profile] = None)

case class GetQuestProposalGiveUpCostRequest(user: User)
case class GetQuestProposalGiveUpCostResult(allowed: ProfileModificationResult, cost: Assets)

case class RewardQuestProposalAuthorRequest(quest: Quest, author: User)
case class RewardQuestProposalAuthorResult()

private[domain] trait ProposeQuestAPI { this: DomainAPIComponent#DomainAPI with DBAccessor =>

  /**
   * Get cost of next quest purchase.
   */
  def getQuestThemeCost(request: GetQuestThemeCostRequest): ApiResult[GetQuestThemeCostResult] = handleDbException {
    import request._

    OkApiResult(Some(GetQuestThemeCostResult(OK, user.costOfPurchasingQuestProposal)))
  }

  /**
   * Purchase quest theme. Check for all conditions are meat.
   * Returns purchased quest theme.
   */
  def purchaseQuestTheme(request: PurchaseQuestThemeRequest): ApiResult[PurchaseQuestThemeResult] = handleDbException {

    request.user.canPurchaseQuestProposals match {
      case OK => {

        val themeCost = request.user.costOfPurchasingQuestProposal

        adjustAssets(AdjustAssetsRequest(user = request.user, cost = Some(themeCost))) map { r =>
          val user = r.user
          val t = r.user.getRandomThemeForQuestProposal
          val reward = r.user.rewardForMakingQuest

          val u = user.copy(
            profile = user.profile.copy(
              questProposalContext = user.profile.questProposalContext.copy(
                approveReward = reward,
                numberOfPurchasedThemes = user.profile.questProposalContext.numberOfPurchasedThemes + 1,
                purchasedTheme = Some(ThemeWithID(t.id, t)))))
          db.user.update(u)

          OkApiResult(Some(PurchaseQuestThemeResult(OK, Some(u.profile))))
        }

      }
      case a => OkApiResult(Some(PurchaseQuestThemeResult(a)))
    }
  }

  /**
   * Get cost of proposing quest.
   */
  def getQuestThemeTakeCost(request: GetQuestThemeTakeCostRequest): ApiResult[GetQuestThemeTakeCostResult] = handleDbException {
    import request._

    OkApiResult(Some(GetQuestThemeTakeCostResult(OK, user.costOfTakingQuestTheme)))
  }

  /**
   * Takes currently purchased theme to make a quest with it.
   */
  def takeQuestTheme(request: TakeQuestThemeRequest): ApiResult[TakeQuestThemeResult] = handleDbException {

    request.user.canTakeQuestTheme match {

      case OK => {

        adjustAssets(AdjustAssetsRequest(user = request.user, cost = Some(request.user.costOfTakingQuestTheme))) map { r =>
          val user = r.user
          val pt = user.profile.questProposalContext.purchasedTheme

          val u = user.copy(
            profile = user.profile.copy(
              questProposalContext = user.profile.questProposalContext.copy(
                numberOfPurchasedThemes = 0,
                purchasedTheme = None,
                takenTheme = pt,
                questProposalCooldown = user.getCooldownForTakeTheme)))
          db.user.update(u)

          OkApiResult(Some(TakeQuestThemeResult(OK, Some(u.profile))))
        }
      }

      case (a: ProfileModificationResult) => OkApiResult(Some(TakeQuestThemeResult(a)))
    }
  }

  /**
   * Takes currently purchased theme to make a quest with it.
   */
  def proposeQuest(request: ProposeQuestRequest): ApiResult[ProposeQuestResult] = handleDbException {

    request.user.canProposeQuest(ContentType.withName(request.quest.media.contentType)) match {
      case OK => {

        db.quest.create(
          Quest(
            themeID = request.user.profile.questProposalContext.takenTheme.get.id,
            authorUserID = request.user.id,
            approveReward = request.user.profile.questProposalContext.approveReward,
            info = QuestInfo(
              content = request.quest)))

        val u = request.user.copy(
          profile = request.user.profile.copy(
            questProposalContext = request.user.profile.questProposalContext.copy(
              numberOfPurchasedThemes = 0,
              purchasedTheme = None,
              takenTheme = None)))

        db.user.update(u)

        OkApiResult(Some(ProposeQuestResult(OK, Some(u.profile))))
      }
      case (a: ProfileModificationResult) => OkApiResult(Some(ProposeQuestResult(a)))
    }
  }

  /**
   * Give up quest proposal for the user if he is going to make one.
   */
  def giveUpQuestProposal(request: GiveUpQuestProposalRequest): ApiResult[GiveUpQuestProposalResult] = handleDbException {
    import request._

    user.canGiveUpQuestProposal match {
      case OK => {

        adjustAssets(AdjustAssetsRequest(user = user, cost = Some(user.costOfGivingUpQuestProposal))) map { r =>

          val u = r.user.copy(
            profile = user.profile.copy(
              questProposalContext = user.profile.questProposalContext.copy(
                numberOfPurchasedThemes = 0,
                purchasedTheme = None,
                takenTheme = None)))
          db.user.update(u)

          OkApiResult(Some(GiveUpQuestProposalResult(OK, Some(u.profile))))
        }

      }

      case (a: ProfileModificationResult) => OkApiResult(Some(GiveUpQuestProposalResult(a)))
    }
  }

  /**
   * Get cost for giving up quest proposal.
   */
  def getQuestProposalGiveUpCost(request: GetQuestProposalGiveUpCostRequest): ApiResult[GetQuestProposalGiveUpCostResult] = handleDbException {
    import request._

    OkApiResult(Some(GetQuestProposalGiveUpCostResult(OK, user.costOfGivingUpQuestProposal)))
  }

  /**
   * Give quest proposal author a reward on quest status change
   */
  def rewardQuestProposalAuthor(request: RewardQuestProposalAuthorRequest): ApiResult[RewardQuestProposalAuthorResult] = handleDbException {
    import request._

    val r = QuestStatus.withName(quest.status) match {
      case QuestStatus.OnVoting => {
        Logger.error("We are rewarding player for proposal what is on voting.")
        InternalErrorApiResult()
      }
      case QuestStatus.InRotation => adjustAssets(AdjustAssetsRequest(user = author, reward = Some(quest.approveReward)))
      case QuestStatus.RatingBanned => OkApiResult(Some(AdjustAssetsResult(author)))
      case QuestStatus.CheatingBanned => adjustAssets(AdjustAssetsRequest(user = author, cost = Some(author.penaltyForCheating)))
      case QuestStatus.IACBanned => adjustAssets(AdjustAssetsRequest(user = author, cost = Some(author.penaltyForIAC)))
      case QuestStatus.OldBanned => OkApiResult(Some(AdjustAssetsResult(author)))
    }

    r map { r =>
      OkApiResult(Some(RewardQuestProposalAuthorResult()))
    }
  }

}

