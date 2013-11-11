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
case class GetQuestThemeCostResult(allowed: ProfileModificationResult, cost: Assets = Assets(0, 0, 0))

case class GetPurchasedQuestThemeRequest(user: User)
case class GetPurchasedQuestThemeResult(theme: Option[Theme])

case class PurchaseQuestThemeRequest(user: User)
case class PurchaseQuestThemeResult(allowed: ProfileModificationResult, theme: Option[Theme] = None)


// TODO IMPLEMENT store in profile all allowed actions so all of them will be requested with single call to profile.

private[domain] trait ProposeQuestAPI { this: DBAccessor =>

  /**
   * Get cost of next quest purchase.
   */
  def getQuestThemeCost(request: GetQuestThemeCostRequest): ApiResult[GetQuestThemeCostResult] = handleDbException {
    import request._

    user.canPurchaseQuestProposals match {
      case OK => OkApiResult(Some(GetQuestThemeCostResult(OK, user.costOfPurchasingQuestProposal)))
      case a => OkApiResult(Some(GetQuestThemeCostResult(a)))
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


