package controllers.domain.app.user

import components._
import controllers.domain.{DomainAPIComponent, _}
import controllers.domain.helpers._
import models.domain.user._
import models.domain.user.message.MessageTaskCompleted
import play.Logger

case class ResetDailyTasksRequest(user: User)
case class ResetDailyTasksResult(user: User)

case class UpdateDailyTasksCompletedFractionRequest(user: User)
case class UpdateDailyTasksCompletedFractionResult(user: User)

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
    val tutorialTasksToCarry =
      user.profile.dailyTasks.tasks.filter(t => t.tutorialTaskId.isDefined && t.currentCount < t.requiredCount)

    db.user.resetTasks(user.id, user.getTasksForTomorrow, user.getResetTasksTimeout) ifSome { u =>

      (if (tutorialTasksToCarry != List.empty) {
        db.user.addTasks(u.id, tutorialTasksToCarry)
      } else {
        Some(u)
      }) ifSome { u =>
        OkApiResult(ResetDailyTasksResult(u))
      }
    }
  }

  /**
   * Recalculates current assigned daily tasks completed fraction.
   */
  private [app] def updateDailyTasksCompletedFraction(request: UpdateDailyTasksCompletedFractionRequest): ApiResult[UpdateDailyTasksCompletedFractionResult] = handleDbException {
    def completedFraction(dt: DailyTasks): Float = {
      dt.tasks.map(t => t.currentCount.toFloat / t.requiredCount).sum / dt.tasks.size
    }

    db.user.setTasksCompletedFraction(id = request.user.id, completedFraction = completedFraction(request.user.profile.dailyTasks)) ifSome { u =>
      OkApiResult(UpdateDailyTasksCompletedFractionResult(u))
    }
  }

  /**
   * Increase by one number of completed tasks for given task. Recalculate percentage and schedule reward if all tasks are completed.
   * Do everything in other words.
   */
  def makeTask(request: MakeTaskRequest): ApiResult[MakeTaskResult] = handleDbException {
    import request._


    def shouldGiveTasksReward(dt: DailyTasks, completedTasks: List[Task]): Boolean = {
      dt.tasks.foldLeft(true)((r, v) => if (v.currentCount >= v.requiredCount) r else false) &&
        completedTasks.foldLeft(false)((r, v) => r || v.triggersReward)
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
          if (shouldGiveTasksReward(u.profile.dailyTasks, completedTasks)) {
            db.user.setTasksRewardReceived(id = u.id, rewardReceived = true)
          } else {
            Some(u)
          }
        }) ifSome { u =>
          // Running API calls.

          // Give reward for currently completed tasks.
          completedTasks.foldLeft[ApiResult[SendMessageResult]](OkApiResult(SendMessageResult(u))) { (r, t) =>
            r map { r =>
              adjustAssets(AdjustAssetsRequest(user = r.user, change = t.reward))
            } map { r =>
              sendMessage(SendMessageRequest(user = r.user, message = MessageTaskCompleted(t.id)))
            }
          } map { r =>
            // give reward for all completed.
            if (shouldGiveTasksReward(r.user.profile.dailyTasks, completedTasks)) {
              {
                adjustAssets(AdjustAssetsRequest(user = r.user, change = r.user.profile.dailyTasks.reward))
              } map { r =>
                sendMessage(SendMessageRequest(r.user, message.MessageAllTasksCompleted()))
              }
            } else {
              OkApiResult(SendMessageResult(r.user))
            }
          } map { r =>
            updateDailyTasksCompletedFraction(UpdateDailyTasksCompletedFractionRequest(r.user))
          } map { r =>
            OkApiResult(MakeTaskResult(r.user))
          }
        }
      }
    }
  }
}

