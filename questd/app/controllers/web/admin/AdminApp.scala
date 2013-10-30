package controllers.web.admin

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.libs.ws._
import play.api.libs.json._
import play.api.templates.Html

object AdminApp extends Controller {

  def index = Action {implicit request => 
    Ok(views.html.admin.index(Menu(request)))
  }
  
  def themes = Action {implicit request => 
    Ok(views.html.admin.themes(Menu(request), "list of themes"))
  }

}

