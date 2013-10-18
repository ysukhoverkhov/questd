package controllers.domain

import models.domain.profile._
import models.domain.user._
import models.store.store._
import models.domain.auth._

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
    OkApiResult(Some(LoginResult(LoginResultCode.Login, "This is the session number")))
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

}

