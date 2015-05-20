package controllers.domain.app.user

import components._
import controllers.domain.helpers._
import controllers.domain.{DomainAPIComponent, _}
import controllers.sn.client.{User => SNUser}
import models.domain.user._
import models.domain.user.auth.CrossPromotedApp
import play.Logger

case class GetAllUsersRequest()
case class GetAllUsersResult(users: Iterator[User])

case class UpdateCrossPromotionRequest(
  user: User,
  snUser: SNUser)
case class UpdateCrossPromotionResult(user: User)

private[domain] trait UserAPI { this: DomainAPIComponent#DomainAPI with DBAccessor =>

  /**
   * Get iterator for all users.
   */
  def getAllUsers(request: GetAllUsersRequest): ApiResult[GetAllUsersResult] = handleDbException {
    OkApiResult(GetAllUsersResult(db.user.all))
  }

  /**
   * Updates cross promotion info of current user.
   */
  def updateCrossPromotion(request: UpdateCrossPromotionRequest): ApiResult[UpdateCrossPromotionResult] = handleDbException {
    import request._

    val appsToAdd = request.snUser.idsInOtherApps.filter{ a =>
      request.user.auth.loginMethods.find(lp => lp.methodName == request.snUser.snName) match {
        case None =>
          Logger.error(s"LOgin method is not present in profile but we've just logged in with it: ${request.snUser.snName}")
          false
        case Some(lm) =>
          !lm.crossPromotion.apps.exists(storedApp => storedApp.appName == a.appName)
      }
    }.map{ a =>
      CrossPromotedApp(appName = a.appName, userId = a.snId)
    }

    (if (appsToAdd.nonEmpty)
      db.user.addCrossPromotions(
        id = user.id,
        snName = request.snUser.snName,
        apps = appsToAdd)
    else
      Some(user)) ifSome { u =>
        OkApiResult(UpdateCrossPromotionResult(u))
    }
  }
}

