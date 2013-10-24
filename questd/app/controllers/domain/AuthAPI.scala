package controllers.domain

import models.domain.profile._
import models.domain.user._
import models.store._
import play.Logger

object AuthAPI {

  /**
   * Login with FB. It performs registration as well if the user is logging in for the first time.
   */
  case class LoginFBParams(fbid: String)
  case class LoginFBResult(session: SessionID)


  def withDBExcepionHandling[T <: ApiResult[_]](f: => T): T = {

    f
  //  def withDbExcepionHandling(_) = {
//    _
  }
  
//  } catch {
//    case ex: StoreException => {
//      Logger.error("DB error during login", ex)
//      InternalErrorApiResult(None)
//    }
//  }
  
  // TODO CRITICAL make WithDBExceptionHandling
  def loginfb(params: LoginFBParams): ApiResult[LoginFBResult] = try {

    def login(user: User) = {
      val uuid = java.util.UUID.randomUUID().toString()
      Store.user.update(user.replaceSessionID(uuid))

      val a = List(1, 2)
      a.foreach(print)

      OkApiResult(Some(LoginFBResult(uuid)))
    }

    Store.user.readByFBid(params.fbid) match {
      case None => {

        Logger.debug("No user with FB id found, creating new one " + params.fbid)

        val newUUID = java.util.UUID.randomUUID().toString()
        val newUser = User(newUUID, params.fbid)
        Store.user.create(newUser)
        Store.user.readByFBid(params.fbid) match {

          case None => {
            Logger.error("Unable to find user just created in DB with fbid " + params.fbid)
            InternalErrorApiResult(None)
          }

          case Some(user) => {

            // TODO: fill profile from fb here.

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

  } catch {
    case ex: StoreException => {
      Logger.error("DB error during login", ex)
      InternalErrorApiResult(None)
    }
    
    case ex: Throwable => {
      Logger.error("Exceptionally inexpected exception", ex)
      InternalErrorApiResult(None)
    }
  }

  /**
   * User for session
   */
  case class UserParams(sessionID: SessionID)
  case class UserResult(user: User)

  def user(params: UserParams): ApiResult[UserResult] = try {

    Store.user.readBySessionID(params.sessionID) match {
      case None => NotAuthorisedApiResult(None)

      case Some(user: User) => OkApiResult(
        Some(UserResult(user)))
    }
  } catch {
    case ex: StoreException => {
      Logger.error("DB error during login", ex)
      InternalErrorApiResult(None)
    }
    
    case ex: Throwable => {
      Logger.error("Exceptionally inexpected exception", ex)
      InternalErrorApiResult(None)
    }
  }

}

