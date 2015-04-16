package controllers.domain.app.user

import components._
import controllers.domain.{DomainAPIComponent, _}
import controllers.domain.helpers._
import models.domain._
import play.Logger

case class ResetDailyTasksRequest(user: User)
case class ResetDailyTasksResult(user: User)

case class MakeTaskRequest(
  user: User,
  taskType: Option[TaskType.Value] = None,
  taskId: Option[String] = None)
case class MakeTaskResult(user: User)

private[domain] trait TasksAPI {
  this: DomainAPIComponent#DomainAPI with DBAccessor =>

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

    def completedFraction(dt: DailyTasks): Float = {
      dt.tasks.map(t => t.currentCount.toFloat / t.requiredCount).sum / dt.tasks.size
    }

    def allTasksCompleted(dt: DailyTasks): Boolean = {
      dt.tasks.foldLeft(true)((r, v) => if (v.currentCount >= v.requiredCount) r else false)
    }

    Logger.trace(s"Making task of type $taskType and/or id $taskId")

    // Running db actions.
    val tasksToIncrease: List[Task] = {
      request.taskType.fold[List[Task]](List.empty) { taskType =>
        user.profile.dailyTasks.tasks.filter { t =>
          (t.taskType == taskType) && (t.requiredCount > t.currentCount)
        }
      }
    } ::: {
      request.taskId.fold[List[Task]](List.empty) { taskId =>
        user.profile.dailyTasks.tasks.filter { t =>
          t.id == taskId && t.requiredCount > t.currentCount
        }
      }
    }

    Logger.trace(s"  Increasing tasks of count: ${tasksToIncrease.length} and values: ${tasksToIncrease.map(t => t.id + " " + t.taskType).mkString(", ")}")

    if (tasksToIncrease.isEmpty) {
      OkApiResult(MakeTaskResult(user))
    } else {
      val (completedTasks, u) = tasksToIncrease.foldLeft[(List[Task], Option[User])](List.empty, Some(user)) {
        case (run, task) =>
          val completed = run._1
          val optUser = run._2
          optUser.fold(run) { u =>
            val updatedUser = db.user.incTask(id = u.id, taskId = task.id)
            if (task.requiredCount - task.currentCount == 1)
              (task :: completed, updatedUser)
            else
              (completed, updatedUser)
          }
      }

      u ifSome { u =>
        runWhileSome(u)(
        { u =>
          db.user.setTasksCompletedFraction(id = u.id, completedFraction = completedFraction(u.profile.dailyTasks))
        },
        { u =>
          if (allTasksCompleted(u.profile.dailyTasks)) {
            db.user.setTasksRewardReceived(id = u.id, rewardReceived = true)
          } else {
            Some(u)
          }
        }) ifSome { u =>
          // Running API calls.

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
                sendMessage(SendMessageRequest(r.user, MessageAllTasksCompleted()))
              }
            } else {
              OkApiResult(SendMessageResult(r.user))
            }
          } map { r =>
            OkApiResult(MakeTaskResult(r.user))
          }
        }
      }
    }
  }
}

