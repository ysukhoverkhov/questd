package controllers.domain.admin

import play.Logger

import components.DBAccessor
import models.store._
import models.domain._
import controllers.domain.helpers._
import controllers.domain._

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

