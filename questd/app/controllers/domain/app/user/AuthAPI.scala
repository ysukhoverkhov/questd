package controllers.domain.app.user

import models.domain._
import models.store._
import play.Logger
import helpers._
import controllers.domain.helpers.exceptionwrappers._
import controllers.domain._
import components._
import controllers.domain.libs.facebook.UserFB

case class LoginFBRequest(userfb: UserFB)
case class LoginFBResult(session: String)

case class UserRequest(userID: Option[String] = None, sessionID: Option[String] = None)
case class UserResult(user: User)

private[domain] trait AuthAPI { this: DBAccessor =>

  /**
   * Login with FB. Or create new one if it doesn't exists.
   */
  def loginfb(params: LoginFBRequest): ApiResult[LoginFBResult] = handleDbException {

    def login(user: User) = {
      val uuid = java.util.UUID.randomUUID().toString()
      val newUser = user.copy(auth = user.auth.copy(session = Some(uuid)))

      db.user.update(newUser)

      val a = List(1, 2)
      a.foreach(print)

      OkApiResult(Some(LoginFBResult(uuid)))
    }

    def genderFromFBUser(u: UserFB) = {
      (u.getGender()) match {
        case "male" => Gender.Male
        case "female" => Gender.Female
        case _ => Gender.Unknown
      }
    }

    Logger.debug("Searching for user in database for login with fbid " + params.userfb.getId())

    db.user.readByFBid(params.userfb.getId()) match {
      case None => {

        Logger.debug("No user with FB id found, creating new one " + params.userfb.getId())

        val newUser = User(
          auth = AuthInfo(
            fbid = Some(params.userfb.getId())),
          profile = Profile(
            bio = Bio(
              name = params.userfb.getFirstName(),
              gender = genderFromFBUser(params.userfb).toString,
              timezone = params.userfb.getTimezone().toInt)))

        db.user.create(newUser)
        db.user.readByFBid(params.userfb.getId()) match {
          case None => {
            Logger.error("Unable to find user just created in DB with fbid " + params.userfb.getId())
            InternalErrorApiResult()
          }

          case Some(user) => {
            Logger.debug("New user with FB created " + user)

            login(user)
          }
        }

      }
      case Some(user) => {
        Logger.debug("Existing user login with FB " + user)
        login(user)
      }
    }
  }

  /**
   * User by session or id. At first we check session
   */
  def getUser(params: UserRequest): ApiResult[UserResult] = handleDbException {

    if (params.sessionID != None) {
      db.user.readBySessionID(params.sessionID.get) match {
        case None => NotAuthorisedApiResult()

        case Some(user: User) => OkApiResult(
          Some(UserResult(user)))
      }
    } else if (params.userID != null) {
      db.user.readByID(params.userID.get) match {
        case None => NotFoundApiResult()

        case Some(user: User) => OkApiResult(
          Some(UserResult(user)))
      }

    } else {
      Logger.error("Wrong request for user.")
      InternalErrorApiResult()
    }

  }

}

