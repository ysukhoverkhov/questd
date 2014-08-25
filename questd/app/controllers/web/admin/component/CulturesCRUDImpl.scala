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

case class CultureForm(
  id: String,
  name: String)

trait CulturesCRUDImpl extends Controller with BaseCRUDImpl[Culture, CultureForm] { this: APIAccessor =>

  protected final val emptyForm: Form[CultureForm] = Form(
    mapping(
      "id" -> text,
      "name" -> nonEmptyText)(CultureForm.apply)(CultureForm.unapply))

  // TODO: change me.
  protected final def formFilledWithObject(id: String) = {
    api.getTheme(GetThemeRequest(id)) match {
      case OkApiResult(GetThemeResult(theme)) => {

        emptyForm.fill(CultureForm(
          id = theme.id.toString,
          name = theme.info.name))
      }
      case _ => emptyForm
    }
  }

  // TODO: implement me.
  protected final def allObjects = {
    Some(List(Culture(name = "name")))
//    api.allThemes(AllThemesRequest(sorted = false)) match {
//
//      case OkApiResult(a: AllThemesResult) =>
//        Some(a.themes.toList)
//
//      case _ => None
//    }
  }

  /**
   * Functions what renders HTML
   */
  protected final def renderFunction(request: Request[AnyContent]) = {
    views.html.admin.cultures(
      Menu(request),
      _ : List[Culture], 
      _ : Form[CultureForm])
  }

  // TODO: implement me.
  protected def deleteObjectWithId(id: String): Unit = {
//    api.deleteTheme(DeleteThemeRequest(id))
    Logger.error("Deleting Object!")
  }
  
  /**
   * Home page of our CRUD
   */
  protected val callToHomePage = controllers.web.admin.routes.CulturesCRUD.cultures("")
  
  /**
   * Create culture from its form.
   */
  protected def updateObjectFromForm(form: CultureForm): Unit = {
    Logger.error("Logging in Object!")
    // TODO: implement me.
    
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
  }

  
  /**
   * Get all cultures
   */
  def cultures = objects _

  /**
   * Delete a culture
   */
  def deleteCultureCB = deleteObjectCB _

  /**
   * Updating a culture
   */
  def createCultureCB = createObjectCB
}

