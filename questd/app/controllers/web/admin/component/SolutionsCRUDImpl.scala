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

trait SolutionsCRUDImpl extends Controller { this: APIAccessor =>

  /**
   * Get all solutions
   */
  def solutions(id: String) = Action { implicit request =>

    // Filling table.
    api.allSolutions(AllSolutionsRequest()) match {

      case OkApiResult(Some(a: AllSolutionsResult)) => Ok(
        views.html.admin.solutions(
          Menu(request),
          a.solutions.toList))

      case _ => Ok("Internal server error - themes not received.")
    }
  }

}
