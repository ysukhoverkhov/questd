package controllers.domain.app.user

import models.domain._
import models.store._
import controllers.domain.DomainAPIComponent
import components._
import controllers.domain._
import controllers.domain.helpers.exceptionwrappers._
import logic._
import play.Logger

case class GetAllUsersRequest()
case class GetAllUsersResult(users: Iterator[User])

case class ResetCountersRequest(user: User)
case class ResetCountersResult()

case class AdjustAssetsRequest(user: User, reward: Option[Assets] = None, cost: Option[Assets] = None)
case class AdjustAssetsResult(user: User)

case class CheckIncreaseLevelRequest(user: User)
case class CheckIncreaseLevelResult(user: User)

case class GetRightsAtLevelsRequest(user: User, levelFrom: Int, levelTo: Int)
case class GetRightsAtLevelsResult(rights: List[Rights])

private[domain] trait ProfileAPI { this: DBAccessor =>

  /**
   * Get iterator for all users.
   */
  def getAllUsers(request: GetAllUsersRequest): ApiResult[GetAllUsersResult] = handleDbException {
    import request._

    OkApiResult(Some(GetAllUsersResult(db.user.all)))
  }

  /**
   * Reset all purchases (quests and themes) overnight.
   */
  def resetCounters(request: ResetCountersRequest): ApiResult[ResetCountersResult] = handleDbException {
    import request._

    db.user.resetCounters(user.id, user.getResetPurchasesTimeout)

    OkApiResult(Some(ResetCountersResult()))
  }

  /**
   * Adjust assets value and performs other modifications on profile because of this.
   */
  def adjustAssets(request: AdjustAssetsRequest): ApiResult[AdjustAssetsResult] = handleDbException {
    import request._

    Logger.debug("API - adjustAssets for user " + user.id)

    val del = reward.getOrElse(Assets()) - cost.getOrElse(Assets())

    val del2 = if ((del + user.profile.assets).rating < 0)
      Assets(del.coins, del.money, -user.profile.assets.rating)
    else
      del

    val u = db.user.addToAssets(user.id, del2).getOrElse {
      Logger.error("API - adjustAssets. Unable to find user in db")
      user
    }

    checkIncreaseLevel(CheckIncreaseLevelRequest(u)) map { r => OkApiResult(Some(AdjustAssetsResult(r.user))) }
  }

  /**
   * Check is user should increase its level and increases it if he should.
   */
  def checkIncreaseLevel(request: CheckIncreaseLevelRequest): ApiResult[CheckIncreaseLevelResult] = handleDbException {
    val u = if (request.user.profile.ratingToNextLevel <= request.user.profile.assets.rating) {
      val user = db.user.levelup(request.user.id, request.user.profile.ratingToNextLevel).getOrElse {
        Logger.error("API - checkIncreaseLevel. Unable to get user after increasing level")
        request.user
      }

      db.user.setNextLevelRatingAndRights(
        user.id,
        user.ratingToNextLevel,
        user.calculateRights).getOrElse {
          Logger.error("API - checkIncreaseLevel. Unable to get user after setting rating to next level.")
          request.user
        }
    } else request.user

    OkApiResult(Some(CheckIncreaseLevelResult(u)))
  }
  
  /**
   * Get rights for user if he would be at level.
   */
  def getRightsAtLevels(request: GetRightsAtLevelsRequest): ApiResult[GetRightsAtLevelsResult] = handleDbException {
    import request._
    
    val rights = for (l <- levelFrom to levelTo) yield {
      val u = user.copy(profile = user.profile.copy(level = l))
      u.calculateRights
    } 
    
    OkApiResult(Some(GetRightsAtLevelsResult(rights.toList)))
  }

}

