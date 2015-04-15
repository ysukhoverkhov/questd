package controllers.domain.app.user

import components._
import controllers.domain.{DomainAPIComponent, _}
import controllers.domain.helpers._
import models.domain._

case class ResetDailyTasksRequest(user: User)
case class ResetDailyTasksResult(user: User)

case class MakeTaskRequest(user: User, taskType: TaskType.Value)
case class MakeTaskResult(user: User)

private[domain] trait TasksAPI { this: DomainAPIComponent#DomainAPI with DBAccessor =>

  /**
   * Resets daily tasks.
   */
  def resetDailyTasks(request: ResetDailyTasksRequest): ApiResult[ResetDailyTasksResult] = handleDbException {
    import request._
    val tutorialTasksToCarry = if (!user.profile.dailyTasks.rewardReceived) {
      user.profile.dailyTasks.tasks.filter(t => t.tutorialTaskId != None && t.currentCount < t.requiredCount)
    } else {
      List.empty
    }

    db.user.resetTasks(user.id, user.getTasksForTomorrow, user.getResetTasksTimeout) ifSome { v =>

      (if (tutorialTasksToCarry != List.empty) {
        db.user.addTasks(user.id, tutorialTasksToCarry)
      } else {
        Some(user)
      }) ifSome { u =>
        OkApiResult(ResetDailyTasksResult(u))
      }
    }
  }


  // TODO: make client tasks and use them to make tasks from client.

  /**
   * Increase by one number of completed tasks for given task. Recalculate percentage and schedule reward if all tasks are completed.
   * Do everything in other words.
   */
  def makeTask(request: MakeTaskRequest): ApiResult[MakeTaskResult] = handleDbException {
    import request._

    def calculatePercent(dt: DailyTasks): Float = {
      dt.tasks.map(t => t.currentCount.toFloat / t.requiredCount).sum / dt.tasks.size
    }

    def allTasksCompleted(dt: DailyTasks): Boolean = {
      dt.tasks.foldLeft(true)((r, v) => if (v.currentCount >= v.requiredCount) r else false)
    }



    val tasksToIncrease = user.profile.dailyTasks.tasks.filter{ t =>
      t.taskType == request.taskType && t.requiredCount > t.currentCount
    }

    val completedTasks = tasksToIncrease.foldLeft[List[Task]](List.empty) { (r, t) =>
      db.user.incTask(id = user.id, taskId = t.id)
      if (t.requiredCount - t.currentCount == 1)
        t :: r
      else
        r
    }

    // TODO: test completing several tasks as once.
    // Give reward for currently completed tasks.
    completedTasks.foldLeft[ApiResult[SendMessageResult]](OkApiResult(SendMessageResult(user))) { (r, t) =>
      r map { r =>
        adjustAssets(AdjustAssetsRequest(user = r.user, reward = Some(t.reward)))
      } map { r =>
        sendMessage(SendMessageRequest(user = r.user, message = MessageTaskCompleted(t.id)))
      }
    } map { r =>
      // give reward for all completed.
      if (allTasksCompleted(r.user.profile.dailyTasks)) {
        {
          adjustAssets(AdjustAssetsRequest(user = r.user, reward = Some(r.user.profile.dailyTasks.reward)))
        } map { r =>
          // TODO: add call to set reward received flag.
          sendMessage(SendMessageRequest(r.user, MessageAllTasksCompleted()))
        }
      } else {
        OkApiResult(SendMessageResult(r.user))
      }
    } map { r =>
      // update percent of completed tasks.
      val newPercent = calculatePercent(r.user.profile.dailyTasks)

      // TODO: db call to update percent here.


      OkApiResult(r)
    } map { r =>
      OkApiResult(MakeTaskResult(r.user))
    }

  }
}

