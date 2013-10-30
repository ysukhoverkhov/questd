package controllers.web.admin.component

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.libs.ws._
import play.api.libs.json._
import models.domain.theme._
import controllers.domain.OkApiResult
import controllers.domain.AllThemesResult

trait ThemesCRUDImpl extends Controller { this: AdminComponent#Admin =>

  def themes = Action { implicit request =>
    
    api.allThemes match {
      
      case OkApiResult(Some(a: AllThemesResult)) => Ok(views.html.admin.themes(Menu(request), a.themes))

      case _ => Ok("Internal server error - themes not received.")
    }

//    List(Theme("id", "Theme", "this is a very long descripion of the theme. yeah. indeed."),
//        Theme("id2", "Theme2", "22222 his is a very long descripion of the theme. yeah. indeed."))
  }
  
  def deleteThemeCB = TODO
  
  def createThemeCB = TODO
  
  def editTheme = TODO
  
  def editThemeCB = TODO

}

