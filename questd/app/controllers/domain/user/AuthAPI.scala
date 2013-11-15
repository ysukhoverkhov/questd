package controllers.domain.user

import models.domain._
import models.store._
import play.Logger
import helpers._
import controllers.domain.helpers.exceptionwrappers._
import controllers.domain._
import components._


case class LoginFBRequest(fbid: String)
case class LoginFBResult(session: String)

case class UserRequest(sessionID: String)
case class UserResult(user: User)


private [domain] trait AuthAPI { this: DBAccessor => 


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

    Logger.debug("Searching for user in database for login with fbid " + params.fbid)

    db.user.readByFBid(params.fbid) match {
      case None => {

        Logger.debug("No user with FB id found, creating new one " + params.fbid)

        val newUser = User(auth = AuthInfo(fbid = Some(params.fbid)))
        db.user.create(newUser)
        db.user.readByFBid(params.fbid) match {

          case None => {
            Logger.error("Unable to find user just created in DB with fbid " + params.fbid)
            InternalErrorApiResult(None)
          }

          case Some(user) => {

            // IMPLEMENT fill profile from fb here.

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
   * User for session
   */
  def user(params: UserRequest): ApiResult[UserResult] = handleDbException {

    db.user.readBySessionID(params.sessionID) match {
      case None => NotAuthorisedApiResult(None)

      case Some(user: User) => OkApiResult(
        Some(UserResult(user)))
    }
  }

}


