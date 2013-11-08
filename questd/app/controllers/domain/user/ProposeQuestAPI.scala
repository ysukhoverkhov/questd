package controllers.domain.user

// TODO update to play 2.2.1

import models.domain._
import models.store._
import play.Logger
import helpers._
import controllers.domain.helpers.exceptionwrappers._
import controllers.domain._
import components._
import logic._
import ProfileModificationResult._

case class GetQuestThemePurchaseCostRequest(user: User)
case class GetQuestThemePurchaseCostResult(allowed: ProfileModificationResult, cost: Cost = Cost(0, 0, 0))

case class PurchaseQuestThemeRequest(user: User)
case class PurchaseQuestThemeResult(allowed: ProfileModificationResult, theme: Option[Theme] = None)

case class GetPurchasedQuestThemeRequest(user: User)
case class GetPurchasedQuestThemeResult(theme: Option[Theme])

// TODO IMPLEMENT store in profile all allowed actions so all of them will be requested with single call.

private[domain] trait ProposeQuestAPI { this: DBAccessor =>

  /**
   * Get cost of next quest purchase.
   */
  def getGetQuestThemePurchaseCost(request: GetQuestThemePurchaseCostRequest): ApiResult[GetQuestThemePurchaseCostResult] = handleDbException {
    import request._

    user.canPurchaseQuestProposals match {
      case OK => OkApiResult(Some(GetQuestThemePurchaseCostResult(OK, user.costOfPurchasingQuestProposal)))
      case a => OkApiResult(Some(GetQuestThemePurchaseCostResult(a)))
    }
  }

  /**
   * Return currently purchased quest theme.
   */
  def getPurchasedQuestTheme(request: GetPurchasedQuestThemeRequest): ApiResult[GetPurchasedQuestThemeResult] = handleDbException {
    import request._

    OkApiResult(Some(GetPurchasedQuestThemeResult(user.questProposalContext.purchasedTheme)))
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

        {
          // TODO store money here.
          db.user.updateUser {
            user.copy(questProposalContext = user.questProposalContext.copy(
              numberOfPurchasedThemes = user.questProposalContext.numberOfPurchasedThemes + 1,
              purchasedTheme = Some(t)))

          }
        }

        OkApiResult(Some(PurchaseQuestThemeResult(OK, Some(t))))

      }
      case a => OkApiResult(Some(PurchaseQuestThemeResult(a)))
    }
  }

}


