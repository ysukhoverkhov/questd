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

case class PurchaseQuestThemeRequest(user: User)
case class PurchaseQuestThemeResult(allowed: ProfileModificationResult, theme: Option[Theme] = None)

case class TakeQuestThemeRequest(user: User)
case class TakeQuestThemeResult(allowed: ProfileModificationResult, theme: Option[Theme] = None)

case class ProposeQuestRequest(user: User, quest: QuestInfo)
case class ProposeQuestResult(allowed: ProfileModificationResult)

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

        {
          db.user.updateUser {
            user.copy(
              profile = user.profile.copy(
                assets = user.profile.assets - themeCost,
                questProposalContext = user.profile.questProposalContext.copy(
                  numberOfPurchasedThemes = user.profile.questProposalContext.numberOfPurchasedThemes + 1,
                  purchasedTheme = Some(t))))
          }
        }

        OkApiResult(Some(PurchaseQuestThemeResult(OK, Some(t))))

      }
      case a => OkApiResult(Some(PurchaseQuestThemeResult(a)))
    }
  }

  /**
   * Takes currently purchased theme to make a quest with it.
   */
  def takeQuestTheme(request: TakeQuestThemeRequest): ApiResult[TakeQuestThemeResult] = handleDbException {
    import request._

    user.profile.questProposalContext.purchasedTheme match {
      case None => OkApiResult(Some(TakeQuestThemeResult(InvalidState, None)))
      case Some(pt) => {

        db.user.updateUser {
          user.copy(
            profile = user.profile.copy(
              questProposalContext = user.profile.questProposalContext.copy(
                numberOfPurchasedThemes = 0,
                purchasedTheme = None,
                takenTheme = Some(pt),
                questProposalCooldown = user.getCooldownForTakeTheme)))
        }

        OkApiResult(Some(TakeQuestThemeResult(OK, Some(pt))))
      }
    }
  }

  // TODO report bad request if request is malformed json.
  // TODO implement giving theme up.
  // TODO implement crawler to discard outdated theme.
  // TODO implement theme resolution cooldown (add to disdoc how much it should take to resolve a theme).
  
  case class ProposeQuestRequest(user: User, quest: QuestInfo)
case class ProposeQuestResult(allowed: ProfileModificationResult)

  /**
   * Takes currently purchased theme to make a quest with it.
   */
  def proposeQuest(request: ProposeQuestRequest): ApiResult[ProposeQuestResult] = handleDbException {
    import request._
    
    user.canProposeQuest match {
      case OK => {
        
        db.user.updateUser {
          user.copy(
            profile = user.profile.copy(
              questProposalContext = user.profile.questProposalContext.copy(
                numberOfPurchasedThemes = 0,
                purchasedTheme = None,
                takenTheme = None)))
        }
        
        // TODO add new quest here to DB
        
        
        OkApiResult(Some(ProposeQuestResult(OK)))
      }
      case (a: ProfileModificationResult) => OkApiResult(Some(ProposeQuestResult(a)))
    }
/*
    user.profile.questProposalContext.purchasedTheme match {
      case None => OkApiResult(Some(TakeQuestThemeResult(InvalidState, None)))
      case Some(pt) => {


        OkApiResult(Some(TakeQuestThemeResult(OK, Some(pt))))
      }
    }
    * 
    */
  }


}


