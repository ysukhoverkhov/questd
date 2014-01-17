package controllers.domain.app.user

import models.domain._
import models.store._
import controllers.domain.DomainAPIComponent
import components._
import controllers.domain._
import controllers.domain.helpers.exceptionwrappers._
import logic._
import play.Logger
import controllers.domain.app.protocol.ProfileModificationResult._

case class GetShortlistRequest(
  user: User)
case class GetShortlistResult(
  allowed: ProfileModificationResult,
  userIds: List[String])

case class CostToShortlistRequest(
  user: User)
case class CostToShortlistResult(
  allowed: ProfileModificationResult,
  cost: Option[Assets] = None)

private[domain] trait ShortlistAPI { this: DBAccessor =>

  /**
   * Reset all purchases (quests and themes) overnight.
   */
  //  def resetCounters(request: ResetCountersRequest): ApiResult[ResetCountersResult] = handleDbException {
  //    import request._
  //
  //    db.user.resetCounters(user.id, user.getResetPurchasesTimeout)
  //
  //    OkApiResult(Some(ResetCountersResult()))
  //  }

  /**
   * Get ids of users from our shortlist.
   */
  def getShortlist(request: GetShortlistRequest): ApiResult[GetShortlistResult] = handleDbException {
    OkApiResult(Some(GetShortlistResult(
      allowed = OK,
      userIds = request.user.shortlist)))
  }

  /**
   * How much it'll take to shortlist person.
   */
  def costToShortlist(request: CostToShortlistRequest): ApiResult[CostToShortlistResult] = handleDbException {
    OkApiResult(Some(CostToShortlistResult(
      allowed = OK,
      cost = Some(request.user.costToShortlist))))
  }

  /*


        request.user.canShortlist match {
      case OK => {

//        val themeCost = user.costOfPurchasingQuestProposal
//
//        adjustAssets(AdjustAssetsRequest(user = user, cost = Some(themeCost))) map { r =>
//          val user = r.user
//          val t = r.user.getRandomThemeForQuestProposal
//          val reward = r.user.rewardForMakingApprovedQuest
//          val sampleQuest = {
//            val all = db.quest.allWithStatusAndThemeByPoints(QuestStatus.InRotation.toString, t.id)
//            if (all.hasNext) {
//              Some(all.next.info)
//            } else {
//              None
//            }
//          }
//
//          val u = db.user.purchaseQuestTheme(user.id, ThemeWithID(t.id, t), sampleQuest, reward)
//          OkApiResult(Some(PurchaseQuestThemeResult(OK, u.map(_.profile))))
        }

      }
      case a => OkApiResult(Some(GetShortlistResult(a)))
    }
*/

}

