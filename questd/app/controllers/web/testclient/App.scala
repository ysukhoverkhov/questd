package controllers.web.testclient

import play.api._
import play.api.mvc._
import play.api.libs.ws._

import play.api.libs.concurrent.Execution.Implicits.defaultContext


object App extends Controller {

  def main = Action.async { implicit request =>

    val cook = request.headers.get(COOKIE) match {
      case None => ""
      case Some(c: String) => c
    }
    
    WS.url(controllers.web.rest.routes.ProfileWS.getName.absoluteURL(false))
      .withHeaders(COOKIE -> cook)
      .post("")
      .map(result => {
        result.status match {
          case 401 => Ok(views.html.testclient.clientmain("unauth"))
          case _ => Ok(views.html.testclient.clientmain(result.body))
        }
      })

    
  }

}
