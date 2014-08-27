package controllers.web.admin.component

import controllers.domain.admin.{AllSolutionsRequest, AllSolutionsResult}
import controllers.domain.{DomainAPIComponent, OkApiResult}
import play.api.mvc._

class SolutionsCRUDImpl(val api: DomainAPIComponent#DomainAPI) extends Controller {

  /**
   * Get all solutions
   */
  def solutions(id: String) = Action { implicit request =>

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

