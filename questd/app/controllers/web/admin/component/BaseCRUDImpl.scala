package controllers.web.admin.component

import play.api.mvc._
import play.api.data._
import play.twirl.api.HtmlFormat.Appendable

trait BaseCRUDImpl[DT, FT] extends SecurityAdminImpl {

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
  def objects(id: String) = Authenticated { implicit request =>

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
  def deleteObjectCB(id: String) = Authenticated { implicit request =>
    deleteObjectWithId(id)
    Redirect(callToHomePage)
  }

  /**
   * Create theme action
   */
  def createObjectCB = Authenticated { implicit request =>
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

