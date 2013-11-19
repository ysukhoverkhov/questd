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

case class GetQuestCostRequest(user: User)
case class GetQuestCostResult(allowed: ProfileModificationResult, cost: Assets = Assets(0, 0, 0))

case class PurchaseQuestRequest(user: User)
case class PurchaseQuestResult(allowed: ProfileModificationResult, quest: Option[Quest] = None)

case class TakeQuestRequest(user: User)
case class TakeQuestResult(allowed: ProfileModificationResult, theme: Option[Quest] = None)

case class GetTakeQuestCostRequest(user: User)
case class GetTakeQuestCostResult(allowed: ProfileModificationResult, cost: Assets = Assets(0, 0, 0))

case class ProposeSolutionRequest(user: User, solution: QuestSolution)
case class ProposeSolutionResult(allowed: ProfileModificationResult)

case class GetQuestGiveUpCostRequest(user: User)
case class GetQuestGiveUpCostResult(allowed: ProfileModificationResult, cost: Assets = Assets(0, 0, 0))

case class GiveUpQuestRequest(user: User)
case class GiveUpQuestResult(allowed: ProfileModificationResult)



private[domain] trait SolveQuestAPI { this: DBAccessor =>

 
  def getQuestCost(request: GetQuestCostRequest): ApiResult[GetQuestCostResult] = handleDbException {
    import request._

    OkApiResult(Some(GetQuestCostResult(OK, user.costOfPurchasingQuest)))
  }
  
  def purchaseQuest(request: PurchaseQuestRequest): ApiResult[PurchaseQuestResult] = handleDbException {
    import request._

    OkApiResult(Some(PurchaseQuestResult(OK)))
  }
  
  def getTakeQuestCost(request: GetTakeQuestCostRequest): ApiResult[GetTakeQuestCostResult] = handleDbException {
    import request._

    OkApiResult(Some(GetTakeQuestCostResult(OK, Assets(1, 2, 3))))
  }
  
  def takeQuest(request: TakeQuestRequest): ApiResult[TakeQuestResult] = handleDbException {
    import request._

    OkApiResult(Some(TakeQuestResult(OK)))
  }
  

  def proposeSolution(request: ProposeSolutionRequest): ApiResult[ProposeSolutionResult] = handleDbException {
    import request._

    OkApiResult(Some(ProposeSolutionResult(OK)))
  }
  
  
  def getQuestGiveUpCost(request: GetQuestGiveUpCostRequest): ApiResult[GetQuestGiveUpCostResult] = handleDbException {
    import request._

    OkApiResult(Some(GetQuestGiveUpCostResult(OK, Assets(1, 2, 3))))
  }
  
  def giveUpQuest(request: GiveUpQuestRequest): ApiResult[GiveUpQuestResult] = handleDbException {
    import request._

    OkApiResult(Some(GiveUpQuestResult(OK)))
  }
  
  
  /*
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
          db.user.update {
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

        db.user.update {
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

  /**
   * Get cost of proposing quest.
   */
  def getQuestProposeCost(request: GetQuestProposeCostRequest): ApiResult[GetQuestProposeCostResult] = handleDbException {
    import request._

    OkApiResult(Some(GetQuestProposeCostResult(OK, user.costOfProposingQuest)))
  }

  /**
   * Takes currently purchased theme to make a quest with it.
   */
  def proposeQuest(request: ProposeQuestRequest): ApiResult[ProposeQuestResult] = handleDbException {
    import request._

    user.canProposeQuest(ContentType.apply(quest.content.contentType)) match {
      case OK => {

        db.quest.create(Quest(info = quest))

        db.user.update {
          user.copy(
            profile = user.profile.copy(
              questProposalContext = user.profile.questProposalContext.copy(
                numberOfPurchasedThemes = 0,
                purchasedTheme = None,
                takenTheme = None),
              assets = user.profile.assets - user.costOfProposingQuest))
        }

        OkApiResult(Some(ProposeQuestResult(OK)))
      }
      case (a: ProfileModificationResult) => OkApiResult(Some(ProposeQuestResult(a)))
    }
  }

  /**
   * Give up quest proposal for the user if he is going to make one.
   */
  def giveUpQuestProposal(request: GiveUpQuestProposalRequest): ApiResult[GiveUpQuestProposalResult] = handleDbException {
    import request._

    user.canGiveUpQuest match {
      case OK => {
        val newAssets = (user.profile.assets - user.costOfGivingUpQuestProposal).clamp

        db.user.update {
          user.copy(
            profile = user.profile.copy(
              questProposalContext = user.profile.questProposalContext.copy(
                numberOfPurchasedThemes = 0,
                purchasedTheme = None,
                takenTheme = None),
              assets = newAssets))
        }

        OkApiResult(Some(GiveUpQuestProposalResult(OK)))
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
  * 
  */

}



