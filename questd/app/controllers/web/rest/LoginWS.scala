package controllers.web.rest

import play.api._
import play.api.mvc._
import play.api.libs.json._
import controllers.domain._
import controllers.domain.jsonhelpers.AuthAPI._
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import controllers.domain.OkApiResult



object LoginWS extends Controller {

  def login = Action.async(parse.json) { implicit request =>
    {

      request.body.validate[AuthAPI.LoginParams].map {
        case (params) => {
          Future { AuthAPI.login(params) }.map {
            _ match {
              case OkApiResult(body) => body match {
                case Some(result: AuthAPI.LoginResult) => Ok(Json.toJson(result))
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

  // TODO: http://www.playframework.com/documentation/2.1.2/ScalaJsonCombinators
  def register = Action.async(parse.json) { implicit request =>
    {
      

      //      implicit val creatureReads = (
      //        (__ \ "name").read[String] ~
      //        (__ \ "isDead").read[Boolean] ~
      //        (__ \ "weight").read[Float])(Creature)

//      implicit val creatureWrites = (
//        (__ \ "name").write[String] and
//        (__ \ "isDead").write[Boolean] and
//        (__ \ "weight").write[Float])(unlift(Creature.unapply))

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

