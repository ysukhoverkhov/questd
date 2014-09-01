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

trait SolutionsCRUDImpl extends Controller with SecurityAdminImpl { this: APIAccessor =>

  /**
   * Get all solutions
   */
  def solutions(id: String) = Authenticated { implicit request =>

    // Filling table.
    api.allSolutions(AllSolutionsRequest()) match {

      case OkApiResult(a: AllSolutionsResult) => Ok(
        views.html.admin.solutions(
          Menu(request),
          a.solutions.toList.sortBy(_.info.questId)))

      case _ => Ok("Internal server error - themes not received.")
    }
  }

}

