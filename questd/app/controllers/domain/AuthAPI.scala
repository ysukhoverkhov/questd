package controllers.domain

import models.domain.profile._
import models.domain.user._
import models.store.store._

object AuthAPI {

  /*
   * Login
   */
  case class LoginParams(name: String, pass: String)
  case class LoginResult(name: String, pass: String)

  def login(params: LoginParams): ApiResult[LoginResult] = {
    OkApiResult(Some(LoginResult(params.name, params.pass)))
  }

  /*
   * Register
   */
  case class RegisterParams(name: String, pass: String)
  case class RegisterResult(name: String, pass: String)

  def register(params: RegisterParams): ApiResult[RegisterResult] = {

    // 1. Get user with the name.
    // 2. If the name is not exist - create new user.
    // 3. Auth user and return its session.

//    val existingUser = Store.user.read(t);
    
    Store.user.all
    
    val newUser = User(params.name, params.pass)

    OkApiResult(Some(RegisterResult(params.name, params.pass)))
  }

}
