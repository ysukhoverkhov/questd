package controllers.domain

import apiresult._
import scala.concurrent._
import ExecutionContext.Implicits.global

object AuthAPI {

  // TODO change execution context
  def login(name: String, pass: String): ApiResult = {
      OkApiResult(Some (Map ("name" -> name, "password" -> pass)))
  }
  
}
