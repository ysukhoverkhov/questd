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

case class MakeTaskRequest(user: User, taskType: TaskType.Value)
case class MakeTaskResult(user: User)

private[domain] trait TasksAPI { this: DomainAPIComponent#DomainAPI with DBAccessor =>

  /**
   * Resets daily tasks.
   */
  def resetDailyTasks(request: ResetDailyTasksRequest): ApiResult[ResetDailyTasksResult] = handleDbException {
    import request._

    db.user.resetTasks(user.id, user.getTasksForTomorrow, user.getResetTasksTimeout)

    OkApiResult(Some(ResetDailyTasksResult()))
  }

  /**
   * Increase by one number of completed tasks for given task. Recalculate percentage and schedule reward if all tasks are completed.
   * Do everything in other words.
   */
  def makeTask(request: MakeTaskRequest): ApiResult[MakeTaskResult] = handleDbException {
    import request._

    // TODO: test doing nothing if nothing to do.
    if (user.profile.dailyTasks.tasks.count(t => t.taskType == request.taskType && t.currentCount < t.requiredCount) <= 0) {
      // Nothing to do.
      OkApiResult(Some(MakeTaskResult(user)))
    }

    // Creating copy of our results for future calculations.
    val nt: DailyTasks = user.profile.dailyTasks.copy(
      tasks = user.profile.dailyTasks.tasks.map(t => if (t.taskType == request.taskType) t.copy(currentCount = t.currentCount + 1) else t));

    def calculatePercent(dt: DailyTasks): Float = {
      // TODO: implement me.
      0
    }
    
    def isCompleted(dt: DailyTasks): Boolean = {
      // TODO: implement me.
      false
    }

    val newPercent = calculatePercent(nt)
    val completed = isCompleted(nt)

    val r1 = if (completed) {
      adjustAssets(AdjustAssetsRequest(user = request.user, cost = Some(nt.reward)))
    } else {
      OkApiResult(Some(AdjustAssetsResult(user)))
    } 
    
    r1 map { r =>

      // TODO test percent and completed.
      // TODO: test in db test percent and completed.
      val u = db.user.incTask(user.id, taskType.toString, newPercent, completed)

      // TODO: check return internal error if it's None.
      OkApiResult(Some(MakeTaskResult(u.get)))
    }

  }

}

