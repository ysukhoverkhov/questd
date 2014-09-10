package controllers.domain.app.user

import components._
import controllers.domain._
import controllers.domain.helpers._
import controllers.sn.client.SNUser
import models.domain._
import play.Logger

case class LoginRequest(snName: String, snuser: SNUser)

case class LoginResult(session: String)

case class UserRequest(userId: Option[String] = None, sessionId: Option[String] = None)

case class UserResult(user: User)

private[domain] trait AuthAPI {
  this: DomainAPIComponent#DomainAPI with DBAccessor =>

  /**
   * Login with FB. Or create new one if it doesn't exists.
   */
  def login(params: LoginRequest): ApiResult[LoginResult] = handleDbException {

    def login(user: User) = {
      val uuid = java.util.UUID.randomUUID().toString
      db.user.updateSessionId(user.id, uuid)

      // Update here country from time to time.
      updateUserCulture(UpdateUserCultureRequest(user)) ifOk { r =>
        OkApiResult(LoginResult(uuid))
      }

    }


    Logger.debug("Searching for user in database for login with fbid " + params.snuser.snId)

    db.user.readBySNid(params.snName, params.snuser.snId) match {
      case None =>
        Logger.debug("No user with FB id found, creating new one " + params.snuser.snId)

        val newUser = User(
          auth = AuthInfo(
            snids = Map(params.snName -> params.snuser.snId)),
          profile = Profile(
            publicProfile = PublicProfile(
              bio = Bio(
                name = params.snuser.firstName,
                gender = params.snuser.gender,
                timezone = params.snuser.timezone,
                country = params.snuser.country,
                city = params.snuser.city,
                avatar = Some(
                  ContentReference(contentType = ContentType.Photo, storage = "fb_avatar", reference = params.snuser.snId))))))

        db.user.create(newUser)
        checkIncreaseLevel(CheckIncreaseLevelRequest(newUser))

        db.user.readBySNid(params.snName, params.snuser.snId) match {
          case None =>
            Logger.error("Unable to find user just created in DB with fbid " + params.snuser.snId)
            InternalErrorApiResult()

          case Some(user) =>
            Logger.debug("New user with FB created " + user)
            login(user)
        }

      case Some(user) =>
        Logger.debug("Existing user login with FB " + user)
        login(user)
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
        Logger.error("Wrong request for user.")
        InternalErrorApiResult()
    }
  }

}


