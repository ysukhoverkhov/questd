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
  protected def deleteObjectWithId(id: String): Unit
  protected def updateObjectFromForm(form: FT): Unit
  protected def callToHomePage: Call

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

    // Filling table.
    allObjects match {
      case Some(o) => Ok(renderFunction(request)(o, form))
      case _ => Ok("Internal server error - themes not received.")
    }
  }

  /**
   * Delete object action
   */
  def deleteObjectCB(id: String) = Action { implicit request =>
    deleteObjectWithId(id)
    Redirect(callToHomePage)
  }

  /**
   * Create theme action
   */
  def createObjectCB = Action { implicit request =>
    emptyForm.bindFromRequest.fold(

      formWithErrors => {

        allObjects match {
          case Some(o) => BadRequest(renderFunction(request)(o, formWithErrors))
          case _ => Ok("Internal server error - themes not received.")
        }
      },

      themeForm => {
        updateObjectFromForm(themeForm)

        Redirect(callToHomePage)
      })
  }

}

