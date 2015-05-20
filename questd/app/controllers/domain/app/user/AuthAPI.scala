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

private[domain] trait AuthAPI {
  this: DomainAPIComponent#DomainAPI with DBAccessor =>

  /**
   * Login with FB. Or create new one if it doesn't exists.
   */
  def login(request: LoginRequest): ApiResult[LoginResult] = handleDbException {

    def loginUser(user: User) = {
      val uuid = java.util.UUID.randomUUID().toString

      db.user.updateSessionId(user.id, uuid) ifSome { u =>
        {
          updateCrossPromotion(UpdateCrossPromotionRequest(user, request.snuser))
        } map {
          updateUserCulture(UpdateUserCultureRequest(user))
        } map {
          api
            .processFriendshipInvitationsFromSN(ProcessFriendshipInvitationsFromSNRequest(user, request.snuser)) match {
            case InternalErrorApiResult(a) =>
              InternalErrorApiResult[LoginResult](a)
            case _ =>
              OkApiResult(LoginResult(uuid, user.id))
          }
        }
      }
    }

    // TODO: This should be moved to UserAPI call like "createUser" what should be called from "login".
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

}

