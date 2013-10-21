package controllers.domain

import models.domain.profile._
import models.domain.user._
import models.store.store._

object AuthAPI {

  /*
   * Login
   */
  case class LoginParams(name: String, pass: String)

  object LoginResultCode extends Enumeration {
    val Login = Value(1)
    val Failed = Value(2)
  }
  case class LoginResult(result: LoginResultCode.Value, session: SessionID) 

  def login(params: LoginParams): ApiResult[LoginResult] = {
    
    val loginAttempt = User(params.name, params.pass)
    Store.user.read(loginAttempt) match {
      case None => {
        OkApiResult(Some(LoginResult(LoginResultCode.Failed, "")))
      }
      case Some(userFromDB) => {
        if (userFromDB.password == loginAttempt.password) {
          val uuid = java.util.UUID.randomUUID().toString()
          
          Store.user.update(userFromDB.replaceSessionID(uuid))
          
          OkApiResult(Some(LoginResult(LoginResultCode.Login, uuid)))
        } else {
          OkApiResult(Some(LoginResult(LoginResultCode.Failed, "")))
        } 
      }
    }
  }

  /*
   * Register
   */
  case class RegisterParams(name: String, pass: String)
  
  object RegisterResultCode extends Enumeration {
    val Registered = Value(1)
    val NameTaken = Value(2)
  }
  case class RegisterResult(result: RegisterResultCode.Value)

  def register(params: RegisterParams): ApiResult[RegisterResult] = {

    // 1. Get user with the name.
    // 2. If the name is not exist - create new user.

    val newPossibleUser = User(params.name, params.pass)

    Store.user.read(newPossibleUser) match {
      case Some(user) => OkApiResult(Some(RegisterResult(RegisterResultCode.NameTaken)))
      case None => {
        val createdUser = Store.user.create(newPossibleUser)
        
        OkApiResult(Some(RegisterResult(RegisterResultCode.Registered)))
      }
    }
  }
  
  /*
   * User for session
   */
  case class UserParams(userID: UserID)

  object UserResultCode extends Enumeration {
    val Ok = Value(1)
    val SessionNotFound = Value(2)
  }
  case class UserResult(result: UserResultCode.Value, user: User) 

  def user(params: UserParams): ApiResult[UserResult] = {
    OkApiResult(Some(UserResult(UserResultCode.Ok, User("trul", "tralaikin"))))
  }

}

