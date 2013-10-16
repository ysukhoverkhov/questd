package controllers.domain

object AuthAPI {
  
  case class LoginParams(name: String, pass: String)
  case class LoginResult(name: String, pass: String)

  def login(params: LoginParams): ApiResult[LoginResult] = {
      OkApiResult(Some (LoginResult(params.name, params.pass)))
  }

  case class RegisterParams(name: String, pass: String)
  case class RegisterResult(name: String, pass: String)

  def register(params: RegisterParams): ApiResult[RegisterResult] = {
      OkApiResult(Some (RegisterResult(params.name, params.pass)))
  }

}
