package controllers.domain.admin

import play.Logger

import components.DBAccessor
import models.store._
import models.domain._
import controllers.domain.helpers.exceptionwrappers._
import controllers.domain._

case class AllSolutionsRequest()
case class AllSolutionsResult(solutions: Iterator[QuestSolution])

private[domain] trait SolutionsAdminAPI { this: DBAccessor =>

  /**
   * List all solutions
   */
  def allSolutions(request: AllSolutionsRequest): ApiResult[AllSolutionsResult] = handleDbException {
    Logger.debug("Admin request for all Solutions.")

    OkApiResult(Some(AllSolutionsResult(db.solution.all)))
  }

}

