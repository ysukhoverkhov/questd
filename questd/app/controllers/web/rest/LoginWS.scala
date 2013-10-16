package controllers.web.rest

import play.api._
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._

import controllers.domain._
import controllers.domain.apiresult._
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext

object LoginWS extends Controller {

  implicit val rds = (
    (__ \ 'name).read[String] and
    (__ \ 'pass).read[String]) tupled

  def login = Action.async(parse.json) { implicit request =>
    {
      request.body.validate[(String, String)].map {
        case (name, pass) => {
          Future { AuthAPI.login(name, pass) }.map {
            _ match {
              case OkApiResult(body) => body match {
                case Some(map) => Ok(Json.toJson(map))
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

  
  def register = Action { request =>
    {
      request.body.asJson match {
        case Some(text) => Ok(text)
        case None => BadRequest
      }
    }
  }

}

