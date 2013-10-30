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

case class ThemeForm(text: String, comment: String)

trait ThemesCRUDImpl extends Controller { this: AdminComponent#Admin =>

  val newThemeForm = Form(
    mapping(
      "text" -> nonEmptyText,
      "comment" -> nonEmptyText)(ThemeForm.apply)(ThemeForm.unapply))

      
  def themes = Action { implicit request =>

    api.allThemes match {

      case OkApiResult(Some(a: AllThemesResult)) => Ok(
        views.html.admin.themes(Menu(request),
          a.themes,
          newThemeForm))

      case _ => Ok("Internal server error - themes not received.")
    }

  }

  def deleteThemeCB = TODO

  def createThemeCB = Action { implicit request =>  
    newThemeForm.bindFromRequest.fold(

      formWithErrors => {
          BadRequest(views.html.admin.themes(
              Menu(request),
              List(),
              formWithErrors))
      },
      
      themeForm => {
        val theme = Theme(ThemeID.default, themeForm.text, themeForm.comment)
        api.createTheme(theme)
        
        Redirect(controllers.web.admin.routes.ThemesCRUD.themes)
      })
  }

  def editTheme = TODO

  def editThemeCB = TODO

}

