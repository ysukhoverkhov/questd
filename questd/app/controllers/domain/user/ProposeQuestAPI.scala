package controllers.domain.user

import models.domain._
import models.store._
import play.Logger
import helpers._
import controllers.domain.helpers.exceptionwrappers._
import controllers.domain._
import components._


case class GetQuestThemePurchaseCostRequest()
case class GetQuestThemePurchaseCostResult(cost: Cost)

case class PurchaseQuestThemeRequest()
case class PurchaseQuestThemeResult()

case class GetPurchasedQuestThemeRequest()
case class GetPurchasedQuestThemeResult()


private [domain] trait ProposeQuestAPI { this: DBAccessor => 


  def getGetQuestThemePurchaseCost(request: GetQuestThemePurchaseCostRequest): ApiResult[GetQuestThemePurchaseCostResult] = handleDbException{
    OkApiResult(Some(GetQuestThemePurchaseCostResult(Cost(0, 0, 0))))
  }

  def getPurchasedQuestTheme(request: GetPurchasedQuestThemeRequest): ApiResult[GetPurchasedQuestThemeRequest] = handleDbException{
    OkApiResult(Some(GetPurchasedQuestThemeRequest()))
  }

  /**
   * Purchase quest theme. Check for all conditions are meat.
   * Returns purchased quest theme.
   */
  def purchaseQuestTheme(params: PurchaseQuestThemeRequest): ApiResult[PurchaseQuestThemeResult] = handleDbException {

    OkApiResult(Some(PurchaseQuestThemeResult()))
//    def login(user: User) = {
//      val uuid = java.util.UUID.randomUUID().toString()
//      db.user.updateUser(user.replaceSessionID(uuid))
//
//      val a = List(1, 2)
//      a.foreach(print)
//
//      OkApiResult(Some(LoginFBResult(uuid)))
//    }
//
//    Logger.debug("Searching for user in database for login with fbid " + params.fbid)
//
//    db.user.readUserByFBid(params.fbid) match {
//      case None => {
//
//        Logger.debug("No user with FB id found, creating new one " + params.fbid)
//
//        val newUUID = java.util.UUID.randomUUID().toString()
//        val newUser = User(newUUID, params.fbid)
//        db.user.createUser(newUser)
//        db.user.readUserByFBid(params.fbid) match {
//
//          case None => {
//            Logger.error("Unable to find user just created in DB with fbid " + params.fbid)
//            InternalErrorApiResult(None)
//          }
//
//          case Some(user) => {
//
//            // TODO IMPLEMENT fill profile from fb here.
//
//            Logger.debug("New user with FB created " + user)
//
//            login(user)
//          }
//        }
//
//      }
//      case Some(user) => {
//        Logger.debug("Existing user login with FB " + user)
//        login(user)
//      }
//    }
  }
/*
  /**
   * User for session
   */
  def user(params: UserParams): ApiResult[UserResult] = handleDbException {

    db.user.readUserBySessionID(params.sessionID) match {
      case None => NotAuthorisedApiResult(None)

      case Some(user: User) => OkApiResult(
        Some(UserResult(user)))
    }
  }
*/
}


