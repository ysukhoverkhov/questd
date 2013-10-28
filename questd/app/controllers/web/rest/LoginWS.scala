package controllers.web.rest

import play.api._
import play.api.mvc._

import controllers.web.rest.component.WSComponent
import controllers.componentregistry.ComponentRegistrySingleton


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


// TODO CRITICAL Write tests for WS with mock API. It's better to do with external ws call. read play docs on testing WS
object LoginWS extends Controller {
  
  val wsimpl: WSComponent#WS = ComponentRegistrySingleton.ws

  def loginfb = wsimpl.loginfb

}

