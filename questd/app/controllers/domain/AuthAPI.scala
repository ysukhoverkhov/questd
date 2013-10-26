package controllers.domain

import models.domain.profile._
import models.domain.user._
import models.store._
import play.Logger
import helpers._


// TODO CRITICAL Write tests for API with mock database - Implement mock implementation of db and paremetrize it with mock daos.
// http://etorreborre.github.io/specs2/guide/org.specs2.guide.Matchers.html
private [domain] trait AuthAPI { this: DomainAPIComponent#DomainAPI => 

  /**
   * Login with FB. It performs registration as well if the user is logging in for the first time.
   */
  case class LoginFBParams(fbid: String)
  case class LoginFBResult(session: SessionID)



  /**
   * Login with FB. Or create new one if it doesn't exists.
   */
  def loginfb(params: LoginFBParams): ApiResult[LoginFBResult] = handleDbException {

    def login(user: User) = {
      val uuid = java.util.UUID.randomUUID().toString()
      db.updateUser(user.replaceSessionID(uuid))

      val a = List(1, 2)
      a.foreach(print)

      OkApiResult(Some(LoginFBResult(uuid)))
    }

    db.readUserByFBid(params.fbid) match {
      case None => {

        Logger.debug("No user with FB id found, creating new one " + params.fbid)

        val newUUID = java.util.UUID.randomUUID().toString()
        val newUser = User(newUUID, params.fbid)
        db.createUser(newUser)
        db.readUserByFBid(params.fbid) match {

          case None => {
            Logger.error("Unable to find user just created in DB with fbid " + params.fbid)
            InternalErrorApiResult(None)
          }

          case Some(user) => {

            // TODO IMPLEMENT fill profile from fb here.

            Logger.debug("New user with FB created " + user)

            login(user)
          }
        }

      }
      case Some(user) => {
        Logger.debug("User login with FB " + user)
        login(user)
      }
    }
  }

  /**
   * User for session
   */
  case class UserParams(sessionID: SessionID)
  case class UserResult(user: User)

  def user(params: UserParams): ApiResult[UserResult] = handleDbException {

    db.readUserBySessionID(params.sessionID) match {
      case None => NotAuthorisedApiResult(None)

      case Some(user: User) => OkApiResult(
        Some(UserResult(user)))
    }
  }

}


