package controllers.domain.admin

import components.DBAccessor
import controllers.domain._
import controllers.domain.helpers._
import models.domain.solution.Solution
import play.Logger

case class AllSolutionsRequest()
case class AllSolutionsResult(solutions: Iterator[Solution])

private[domain] trait SolutionsAdminAPI { this: DBAccessor =>

  /**
   * List all solutions
   */
  def allSolutions(request: AllSolutionsRequest): ApiResult[AllSolutionsResult] = handleDbException {
    Logger.debug("Admin request for all Solutions.")

    OkApiResult(AllSolutionsResult(db.solution.all))
  }

}

