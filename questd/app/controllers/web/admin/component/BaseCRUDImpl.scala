package controllers.web.admin.component

import play.api._
import play.api.mvc._
import play.api.mvc.Results._
import play.api.data._
import play.api.data.Forms._
import play.api.libs.ws._
import play.api.libs.json._
import models.domain._
import controllers.domain._
import controllers.domain.admin._
import components._
import play.twirl.api.HtmlFormat.Appendable

trait BaseCRUDImpl[DT, FT] { 

  protected def emptyForm: Form[FT]
  protected def formFilledWithObject(id: String): Form[FT] 
  protected def allObjects: Option[List[DT]]
  protected def renderFunction(request: Request[AnyContent]): (List[DT], Form[FT]) => Appendable 
  
  
  /**
   * Get all objects action
   */
  protected def objects(id: String) = Action { implicit request =>

    // Filling form.
    val form = if (id == "") {
      emptyForm
    } else {
      formFilledWithObject(id)
    }

    val objects = allObjects

    // Filling table.
    objects match {
      case Some(o) => Ok(renderFunction(request)(o, form))
      case _ => Ok("Internal server error - themes not received.")
    }
  }

//  /**
//   * Delete theme action
//   */
//  def deleteThemeCB(id: String) = Action { implicit request =>
//
//    api.deleteTheme(DeleteThemeRequest(id))
//
//    Redirect(controllers.web.admin.routes.ThemesCRUD.themes(""))
//  }
//
//  /**
//   * Create theme action
//   */
//  def createThemeCB = Action { implicit request =>
//    newThemeForm.bindFromRequest.fold(
//
//      formWithErrors => {
//        BadRequest(views.html.admin.themes(
//          Menu(request),
//          List(),
//          formWithErrors))
//      },
//
//      themeForm => {
//
//        val theme = Theme(
//          id = themeForm.id,
//          info = ThemeInfo(
//            name = themeForm.name,
//            description = themeForm.description,
//            icon = Some(ContentReference(
//              contentType = ContentType.withName(themeForm.iconType),
//              storage = themeForm.iconStorage,
//              reference = themeForm.iconReference)),
//            media = ContentReference(
//              contentType = ContentType.withName(themeForm.mediaType),
//              storage = themeForm.mediaStorage,
//              reference = themeForm.mediaReference)))
//
//        if (theme.id == "") {
//          api.createTheme(CreateThemeRequest(theme))
//        } else {
//          api.updateTheme(UpdateThemeRequest(theme))
//        }
//
//        Redirect(controllers.web.admin.routes.ThemesCRUD.themes(""))
//      })
//  }

}

