package controllers.web.admin.component

import controllers.domain.admin._
import controllers.domain.{DomainAPIComponent, OkApiResult}
import models.domain._
import play.api.data.Forms._
import play.api.data._
import play.api.mvc._

case class CultureForm(
  id: String,
  name: String,
  cultureToMergeWith: String)

class CulturesCRUDImpl(val api: DomainAPIComponent#DomainAPI) extends Controller with BaseCRUDImpl[Culture, CultureForm] {

  protected final val emptyForm: Form[CultureForm] = Form(
    mapping(
      "id" -> text,
      "name" -> nonEmptyText,
      "cultureToMergeWith" -> text)(CultureForm.apply)(CultureForm.unapply))

  /**
   * Form willed with object.
   */
  protected final def formFilledWithObject(id: String) = {
    api.getCulture(GetCultureRequest(id)) match {
      case OkApiResult(GetCultureResult(c)) =>
        emptyForm.fill(CultureForm(
          id = c.id.toString,
          name = c.name,
          cultureToMergeWith = ""))

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

    val mergeWith = form.cultureToMergeWith

    if (form.id == "") {
      api.createCulture(CreateCultureRequest(Culture(
        id = form.id,
        name = form.name)))
    } else {
      val culture = api.getCulture(GetCultureRequest(form.id)).body.get.culture

      if (mergeWith != "") {
        api.mergeCultureIntoCulture(MergeCultureIntoCultureRequest(culture, mergeWith))
      } else {
        api.updateCulture(UpdateCultureRequest(culture.copy(name = form.name)))
      }
    }
  }
}

