package controllers.domain.app.user

import components._
import controllers.domain.helpers._
import controllers.domain.{DomainAPIComponent, _}
import controllers.sn.client.{User => SNUser}
import models.domain.user._
import models.domain.user.auth.CrossPromotedApp
import models.domain.user.dailyresults.DailyResult
import play.Logger

case class GetUserRequest(userId: Option[String] = None, sessionId: Option[String] = None)
object UserResultCode extends Enumeration {
  val OK, NotFound = Value
}
case class GetUserResult(code: UserResultCode.Value, user: Option[User] = None)

case class GetAllUsersRequest()
case class GetAllUsersResult(users: Iterator[User])

case class UpdateCrossPromotionRequest(
  user: User,
  snUser: SNUser)
case class UpdateCrossPromotionResult(user: User)

case class CreateUserRequest(user: User)
case class CreateUserResult(user: User)

private[domain] trait UserAPI { this: DomainAPIComponent#DomainAPI with DBAccessor =>

  /**
   * User by session or id.
   */
  def getUser(params: GetUserRequest): ApiResult[GetUserResult] = handleDbException {

    (params.sessionId, params.userId) match {
      case (Some(sessionId), None) =>
        db.user.readBySessionId(sessionId) match {
          case None => OkApiResult(GetUserResult(UserResultCode.NotFound))
          case Some(user: User) => OkApiResult(GetUserResult(UserResultCode.OK, Some(user)))
        }

      case (None, Some(userId)) =>
        db.user.readById(userId) match {
          case None => OkApiResult(GetUserResult(UserResultCode.NotFound))
          case Some(user: User) => OkApiResult(GetUserResult(UserResultCode.OK, Some(user)))
        }

      case _ =>
        InternalErrorApiResult("Wrong request for user")
    }
  }


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

  /**
   * Updates cross promotion info of current user.
   */
  def createUser(request: CreateUserRequest): ApiResult[CreateUserResult] = handleDbException {
    def initializeUser(user: User): User = {
      user.copy(
        profile = user.profile.copy(
          rights = user.calculateRights,
          ratingToNextLevel = user.ratingToNextLevel
        ),
        privateDailyResults = List(DailyResult(
          user.getStartOfCurrentDailyResultPeriod)
        ))
    }

    val user = initializeUser(request.user)
    db.user.create(user)

    OkApiResult(CreateUserResult(user))
  }
}

