package controllers.domain.user

import models.domain._
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

case class ProposeQuestRequest(user: User, quest: QuestInfo)
case class ProposeQuestResult(allowed: ProfileModificationResult, profile: Option[Profile] = None)

case class GiveUpQuestProposalRequest(user: User)
case class GiveUpQuestProposalResult(allowed: ProfileModificationResult, profile: Option[Profile] = None)

case class GetQuestProposalGiveUpCostRequest(user: User)
case class GetQuestProposalGiveUpCostResult(allowed: ProfileModificationResult, cost: Assets)

private[domain] trait ProposeQuestAPI { this: DBAccessor =>

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
    import request._

    user.canPurchaseQuestProposals match {
      case OK => {

        val t = user.getRandomThemeForQuestProposal
        val themeCost = user.costOfPurchasingQuestProposal

        val u = user.copy(
          profile = user.profile.copy(
            questProposalContext = user.profile.questProposalContext.copy(
              numberOfPurchasedThemes = user.profile.questProposalContext.numberOfPurchasedThemes + 1,
              purchasedTheme = Some(t)))).giveUserCost(themeCost)

        db.user.update(u)

        OkApiResult(Some(PurchaseQuestThemeResult(OK, Some(u.profile))))

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
    import request._
    user.canTakeQuestTheme match {

      case OK => {

        val pt = user.profile.questProposalContext.purchasedTheme

        val u = user.copy(
          profile = user.profile.copy(
            questProposalContext = user.profile.questProposalContext.copy(
              numberOfPurchasedThemes = 0,
              purchasedTheme = None,
              takenTheme = pt,
              questProposalCooldown = user.getCooldownForTakeTheme)))
          .giveUserCost(user.costOfTakingQuestTheme)

        db.user.update(u)

        OkApiResult(Some(TakeQuestThemeResult(OK, Some(u.profile))))
      }

      case (a: ProfileModificationResult) => OkApiResult(Some(TakeQuestThemeResult(a)))
    }
  }

  /**
   * Takes currently purchased theme to make a quest with it.
   */
  def proposeQuest(request: ProposeQuestRequest): ApiResult[ProposeQuestResult] = handleDbException {
    import request._

    user.canProposeQuest(ContentType.withName(quest.content.contentType)) match {
      case OK => {

        db.quest.create(Quest(info = quest, userID = user.id))

        val u = user.copy(
          profile = user.profile.copy(
            questProposalContext = user.profile.questProposalContext.copy(
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
        val u = user.copy(
          profile = user.profile.copy(
            questProposalContext = user.profile.questProposalContext.copy(
              numberOfPurchasedThemes = 0,
              purchasedTheme = None,
              takenTheme = None)))
          .giveUserCost(user.costOfGivingUpQuestProposal)

        db.user.update(u)

        OkApiResult(Some(GiveUpQuestProposalResult(OK, Some(u.profile))))
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

}



