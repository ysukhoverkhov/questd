package controllers.web.admin.component

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.libs.ws._
import play.api.libs.json._

import models.domain._
import controllers.domain._
import controllers.domain.admin._
import components._

case class ThemeForm(
  id: String,
  text: String,
  comment: String,
  iconType: String,
  iconStorage: String,
  iconReference: String)

trait ThemesCRUDImpl extends Controller { this: APIAccessor =>

  val newThemeForm = Form(
    mapping(
      "id" -> text,
      "text" -> nonEmptyText,
      "comment" -> nonEmptyText,
      "iconType" -> nonEmptyText,
      "iconStorage" -> nonEmptyText,
      "iconReference" -> nonEmptyText)(ThemeForm.apply)(ThemeForm.unapply))

  /**
   * Get all themes action
   */
  def themes(id: String) = Action { implicit request =>

    // Filling form.
    val form = if (id == "") {
      newThemeForm
    } else {
      api.getTheme(GetThemeRequest(id)) match {
        case OkApiResult(Some(GetThemeResult(theme))) => {

          newThemeForm.fill(ThemeForm(
            id = theme.id.toString,
            text = theme.text,
            comment = theme.comment,
            iconType = theme.icon.contentType.toString,
            iconStorage = theme.icon.storage,
            iconReference = theme.icon.reference))
        }
        case _ => newThemeForm
      }
    }

    // Filling table.
    api.allThemes(AllThemesRequest(sorted = false)) match {

      case OkApiResult(Some(a: AllThemesResult)) => Ok(
        views.html.admin.themes(
          Menu(request),
          a.themes.toList,
          form))

      case _ => Ok("Internal server error - themes not received.")
    }
  }

  /**
   * Delete theme action
   */
  def deleteThemeCB(id: String) = Action { implicit request =>

    api.deleteTheme(DeleteThemeRequest(id))

    Redirect(controllers.web.admin.routes.ThemesCRUD.themes(""))
  }

  /**
   * Create theme action
   */
  def createThemeCB = Action { implicit request =>
    newThemeForm.bindFromRequest.fold(

      formWithErrors => {
        BadRequest(views.html.admin.themes(
          Menu(request),
          List(),
          formWithErrors))
      },

      themeForm => {

        val theme = Theme(
          val theme = Theme(
            id = "",
            text = themeForm.text,
            comment = themeForm.comment,
            icon = ContentReference(
              contentType = ContentType.withName(themeForm.iconType),
              storage = themeForm.iconStorage,
              reference = themeForm.iconReference))
          api.createTheme(CreateThemeRequest(theme))
        } else {
          val theme = Theme(
            id = themeForm.id,
            text = themeForm.text,
            comment = themeForm.comment,
            icon = ContentReference(
              contentType = ContentType.withName(themeForm.iconType),
              storage = themeForm.iconStorage,
              reference = themeForm.iconReference))
          api.updateTheme(UpdateThemeRequest(theme))
        }

        Redirect(controllers.web.admin.routes.ThemesCRUD.themes(""))
      })
  }

}

