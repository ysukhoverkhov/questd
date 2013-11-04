package controllers.web.rest

import play.api._
import play.api.mvc._

import controllers.web.rest.component.WSComponent
import components.componentregistry.ComponentRegistrySingleton


// EXAMPLE
/*
  /*
   * LoginParams
   */
  implicit val loginParamReads = (
    (__ \ 'name).read[String] and
    (__ \ 'pass).read[String])(LoginParams)

  /*
     * LoginResult
     */
  implicit val loginResultWrites = new Writes[LoginResult] {
    def writes(c: LoginResult): JsValue = {
      Json.obj(
        "result" -> c.result.id,
        "session" -> c.session.toString)
    }
  }
*/

object LoginWS extends Controller {
  
  val ws: WSComponent#WS = ComponentRegistrySingleton.ws

  def loginfb = ws.loginfb

}

