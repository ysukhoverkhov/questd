package controllers.web.admin.component

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.libs.ws._
import play.api.libs.json._

trait AdminAppImpl extends Controller {

  def index = Action {implicit request => 
    Ok(views.html.admin.index(Menu(request)))
  }
}
