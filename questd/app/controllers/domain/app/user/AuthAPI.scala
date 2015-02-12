package controllers.domain.app.user

import components._
import controllers.domain._
import controllers.domain.helpers._
import controllers.sn.client.{User => SNUser}
import models.domain._
import play.Logger

case class LoginRequest(snName: String, snuser: SNUser)

case class LoginResult(sessionId: String, userId: String)

case class UserRequest(userId: Option[String] = None, sessionId: Option[String] = None)

case class UserResult(user: User)

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
      updateUserCulture(UpdateUserCultureRequest(user)) ifOk {
        // TODO: remove me in 0.40 as far as we populate time line on creation.
        if (user.timeLine.length <= 0)
          populateTimeLineWithRandomThings(PopulateTimeLineWithRandomThingsRequest(user))
        // end of remove

        api.processFriendshipInvitationsFromSN(ProcessFriendshipInvitationsFromSNRequest(user, request.snuser)) match {
          case InternalErrorApiResult(a) =>
            InternalErrorApiResult[LoginResult](a)
          case _ =>
            OkApiResult(LoginResult(uuid, user.id))
        }
      }
    }

    def createUserAndLogin() = {
      Logger.debug("No user with FB id found, creating new one " + request.snuser.snId)

      val newUser = User(
        auth = AuthInfo(
          snids = Map(request.snName -> request.snuser.snId)),
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

      db.user.create(newUser)
      checkIncreaseLevel(CheckIncreaseLevelRequest(newUser)) // TODO: it looks like it should be removed.

      db.user.readBySNid(request.snName, request.snuser.snId) ifSome { user =>
        populateTimeLineWithRandomThings(PopulateTimeLineWithRandomThingsRequest(user)) ifOk { r =>
          Logger.debug("New user with FB created " + user)
          loginUser(user)
        }
      }
    }

    Logger.debug("Searching for user in database for login with fbid " + request.snuser.snId)

    db.user.readBySNid(request.snName, request.snuser.snId) match {
      case None =>
        Logger.debug("New user login with FB")
        createUserAndLogin()

      case Some(user) =>
        Logger.debug("Existing user login with FB " + user)
        loginUser(user)
    }
  }

  /**
   * User by session or id. At first we check session
   */
  def getUser(params: UserRequest): ApiResult[UserResult] = handleDbException {

    (params.sessionId, params.userId) match {
      case (Some(sessionId), None) =>
        db.user.readBySessionId(sessionId) match {
          case None => NotAuthorisedApiResult()
          case Some(user: User) => OkApiResult(UserResult(user))
        }

      case (None, Some(userId)) =>
        db.user.readById(userId) match {
          case None => NotFoundApiResult()
          case Some(user: User) => OkApiResult(UserResult(user))
        }

      case _ =>
        InternalErrorApiResult("Wrong request for user")
    }
  }

}


