package controllers.web.rest

import scala.concurrent.Future
import play.api._
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import controllers.domain._
import controllers.domain.jsonhelpers.AuthAPI._
import controllers.web.rest.security._



object LoginWS extends Controller with SecurityWS {

  def login = Action.async(parse.json) { implicit request =>
    {

      request.body.validate[AuthAPI.LoginParams].map {
        case (params) => {
          Future { AuthAPI.login(params) }.map {
            _ match {
              case OkApiResult(body) => body match {
                case Some(result: AuthAPI.LoginResult) => 
                  storeAuthInfoInResult(
                      Ok(Json.toJson(result)),
                      result)

                case None => InternalServerError
              }
              
              case _ => InternalServerError
            }
          }
        }
      }.recoverTotal {
        e => Future { BadRequest("Detected error:" + JsError.toFlatJson(e)) }
      }
    }
  }

  def register = Action.async(parse.json) { implicit request =>
    {
      request.body.validate[AuthAPI.RegisterParams].map {
        case (params) => {
          Future { AuthAPI.register(params) }.map {
            _ match {
              case OkApiResult(body) => body match {
                case Some(result: AuthAPI.RegisterResult) => Ok(Json.toJson(result))
                case None => Ok
              }
              case _ => InternalServerError
            }
          }
        }
      }.recoverTotal {
        e => Future { BadRequest("Detected error:" + JsError.toFlatJson(e)) }
      }
    }
  }

}

