package controllers.domain.app.user

import components._
import controllers.domain._
import controllers.domain.helpers._
import controllers.sn.client.{User => SNUser}
import models.domain.common.{ContentReference, ContentType}
import models.domain.user._
import models.domain.user.auth.{AuthInfo, LoginMethod}
import play.Logger

case class LoginRequest(snName: String, snuser: SNUser)
case class LoginResult(sessionId: String, userId: String)


case class UserRequest(userId: Option[String] = None, sessionId: Option[String] = None)

object UserResultCode extends Enumeration {
  val OK, NotFound = Value
}
case class UserResult(code: UserResultCode.Value, user: Option[User] = None)


private[domain] trait AuthAPI {
  this: DomainAPIComponent#DomainAPI with DBAccessor =>

  /**
   * Login with FB. Or create new one if it doesn't exists.
   */
  def login(request: LoginRequest): ApiResult[LoginResult] = handleDbException {

    def loginUser(user: User) = {
      val uuid = java.util.UUID.randomUUID().toString
      db.user.updateSessionId(user.id, uuid)

      // Update here country from time to time.
      updateUserCulture(UpdateUserCultureRequest(user)) map {
        api.processFriendshipInvitationsFromSN(ProcessFriendshipInvitationsFromSNRequest(user, request.snuser)) match {
          case InternalErrorApiResult(a) =>
            InternalErrorApiResult[LoginResult](a)
          case _ =>
            OkApiResult(LoginResult(uuid, user.id))
        }
      }
    }

    // This should be moved to seporate API call like "createUser" what should be called from "login".
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

    def createUserAndLogin() = {
      Logger.debug("No user with FB id found, creating new one " + request.snuser.snId)

      val newUser = User(
        auth = AuthInfo(
          loginMethods = List(LoginMethod(
            methodName = request.snName,
            userId = request.snuser.snId))),
        profile = Profile(
          publicProfile = PublicProfile(
            bio = Bio(
              name = request.snuser.firstName,
              gender = request.snuser.gender,
              timezone = request.snuser.timezone,
              country = request.snuser.country,
              city = request.snuser.city,
              avatar = Some(
                ContentReference(contentType = ContentType.Photo, storage = "fb_avatar", reference = request.snuser.snId))))))

      db.user.create(initializeUser(newUser))

      db.user.readBySNid(request.snName, request.snuser.snId) ifSome { user =>
        populateTimeLineWithRandomThings(PopulateTimeLineWithRandomThingsRequest(user)) map { r =>
          Logger.debug(s"New user created with FB: ${user.id} / ${user.profile.publicProfile.bio.name}")
          loginUser(user)
        }
      }
    }

    Logger.debug(s"Searching for user in database for login with FBid ${request.snuser.snId}")

    db.user.readBySNid(request.snName, request.snuser.snId) match {
      case None =>
        Logger.debug("New user login with FB")
        createUserAndLogin()

      case Some(user) =>
        Logger.debug(s"Existing user login with FB: ${user.id} / ${user.profile.publicProfile.bio.name}")
        loginUser(user)
    }
  }

  /**
   * User by session or id.
   */
  def getUser(params: UserRequest): ApiResult[UserResult] = handleDbException {

    (params.sessionId, params.userId) match {
      case (Some(sessionId), None) =>
        db.user.readBySessionId(sessionId) match {
          case None => OkApiResult(UserResult(UserResultCode.NotFound))
          case Some(user: User) => OkApiResult(UserResult(UserResultCode.OK, Some(user)))
        }

      case (None, Some(userId)) =>
        db.user.readById(userId) match {
          case None => OkApiResult(UserResult(UserResultCode.NotFound))
          case Some(user: User) => OkApiResult(UserResult(UserResultCode.OK, Some(user)))
        }

      case _ =>
        InternalErrorApiResult("Wrong request for user")
    }
  }
}

