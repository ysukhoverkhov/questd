package controllers.domain.app.user

import models.domain._
import models.store._
import controllers.domain.DomainAPIComponent
import components._
import controllers.domain._
import controllers.domain.helpers.exceptionwrappers._
import logic._
import play.Logger

case class ResetDailyTasksRequest(user: User)
case class ResetDailyTasksResult()

private[domain] trait TasksAPI { this: DomainAPIComponent#DomainAPI with DBAccessor =>

  /**
   * Shifts daily result.
   */
  def resetDailyTasks(request: ResetDailyTasksRequest): ApiResult[ResetDailyTasksResult] = handleDbException {
    import request._

    // TODO: pass new tasks here.
    db.user.resetTasks(user.id, DailyTasks(), user.getResetTasksTimeout)

    OkApiResult(Some(ResetDailyTasksResult()))
  }
}

