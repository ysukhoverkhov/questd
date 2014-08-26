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

  /**
   * Form willed with obect.
   */
  protected final def formFilledWithObject(id: String) = {
    api.getCulture(GetCultureRequest(id)) match {
      case OkApiResult(GetCultureResult(c)) => {

        emptyForm.fill(CultureForm(
          id = c.id.toString,
          name = c.name))
      }
      case _ => emptyForm
    }
  }

  /**
   * All objects crud is for.
   */
  protected final def allObjects = {
    api.allCultures(AllCulturesRequest()) match {

      case OkApiResult(a: AllCulturesResult) =>
        Some(a.cultures.toList)

      case _ => None
    }
  }

  /**
   * Functions what renders HTML
   */
  protected final def renderFunction(request: Request[AnyContent]) = {
    views.html.admin.cultures(
      Menu(request),
      _: List[Culture],
      _: Form[CultureForm])
  }

  /**
   * Delete object.
   */
  protected def deleteObjectWithId(id: String): Unit = {
        api.deleteCulture(DeleteCultureRequest(id))
  }

  /**
   * Home page of our CRUD
   */
  protected val callToHomePage = controllers.web.admin.routes.CulturesCRUD.cultures("")

  /**
   * Create culture from its form.
   */
  protected def updateObjectFromForm(form: CultureForm): Unit = {

    val culture = Culture(
      id = form.id,
      name = form.name)

    if (culture.id == "") {
      api.createCulture(CreateCultureRequest(culture))
    } else {
      api.updateCulture(UpdateCultureRequest(culture))
    }
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

