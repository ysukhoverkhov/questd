package controllers.domain.app.user

import models.domain._
import controllers.domain.DomainAPIComponent
import components._
import controllers.domain._
import controllers.domain.helpers._
import play.Logger

case class ResetDailyTasksRequest(user: User)
case class ResetDailyTasksResult()

case class MakeTaskRequest(user: User, taskType: Option[TaskType.Value] = None, tutorialTaskId: Option[String] = None)
case class MakeTaskResult(user: User)

private[domain] trait TasksAPI { this: DomainAPIComponent#DomainAPI with DBAccessor =>

  /**
   * Resets daily tasks.
   */
  def resetDailyTasks(request: ResetDailyTasksRequest): ApiResult[ResetDailyTasksResult] = handleDbException {
    import request._
    val (tutorialTasksToCarry, tutorialReward) = if (!user.profile.dailyTasks.rewardReceived) {
      val t = user.profile.dailyTasks.tasks.filter(_.tutorialTask != None)
      (
        t,
        (Assets() /: t)((r, c) => r + c.tutorialTask.get.reward))
    } else {
      (List.empty, Assets())
    }

    db.user.resetTasks(user.id, user.getTasksForTomorrow, user.getResetTasksTimeout) ifSome { v =>

      (if (tutorialTasksToCarry != List.empty) {
        db.user.addTasks(user.id, tutorialTasksToCarry, tutorialReward)
      } else {
        Some(user)
      }) ifSome { v =>
        OkApiResult(ResetDailyTasksResult())
      }
    }
  }

  /**
   * Increase by one number of completed tasks for given task. Recalculate percentage and schedule reward if all tasks are completed.
   * Do everything in other words.
   */
  def makeTask(request: MakeTaskRequest): ApiResult[MakeTaskResult] = handleDbException {
    import request._
    import com.vita.scala.Extensions._

    assert(taskType == None ^^ tutorialTaskId == None, "Both taskType and tutorial task id are None or Some which is wrong.")

    def taskIsAlreadyCompleted = {
      (taskType, tutorialTaskId) match {
        case (Some(tt), None) =>
          user.profile.dailyTasks.tasks.count(t => t.taskType == tt && t.currentCount < t.requiredCount) <= 0

        case (None, Some(ti)) =>
          user.profile.dailyTasks.tasks.count(t => t.tutorialTask != None && t.tutorialTask.get.id == ti && t.currentCount < t.requiredCount) <= 0

        case _ =>
          Logger.error("Incorrect request to makeTest")
          true
      }
    }

    if (taskIsAlreadyCompleted) {

      // Nothing to do.
      OkApiResult(MakeTaskResult(user))

    } else {

      def createUpdatedTasks = {
        (taskType, tutorialTaskId) match {
          case (Some(tt), None) =>
            user.profile.dailyTasks.copy(
              tasks = user.profile.dailyTasks.tasks.map(t => if (t.taskType == tt) t.copy(currentCount = t.currentCount + 1) else t))

          case (None, Some(ti)) =>
            user.profile.dailyTasks.copy(
              tasks = user.profile.dailyTasks.tasks.map(t => if (t.tutorialTask != None && t.tutorialTask.get.id == ti) t.copy(currentCount = t.currentCount + 1) else t))

          case _ =>
            Logger.error("Incorrect request to makeTest")
            user.profile.dailyTasks
        }
      }

      // Creating copy of our results for future calculations.
      val nt: DailyTasks = createUpdatedTasks

      def calculatePercent(dt: DailyTasks): Float = {
        dt.tasks.map(t => t.currentCount.toFloat / t.requiredCount).sum / dt.tasks.size
      }

      def isCompleted(dt: DailyTasks): Boolean = {
        dt.tasks.foldLeft(true)((r, v) => if (v.currentCount >= v.requiredCount) r else false)
      }

      val newPercent = calculatePercent(nt)
      val completed = isCompleted(nt)

      val r1 = if (completed) {
        adjustAssets(AdjustAssetsRequest(user = request.user, reward = Some(nt.reward))) map { r =>
          sendMessage(SendMessageRequest(r.user, MessageTasksCompleted()))
        }
      } else {
        OkApiResult(AdjustAssetsResult(user))
      }

      r1 map { r =>
        val u = (taskType, tutorialTaskId) match {
          case (Some(tt), None) =>
            db.user.incTask(id = user.id, taskType = tt.toString, completed = newPercent, rewardReceived = completed)

          case (None, Some(ti)) =>
            db.user.incTutorialTask(id = user.id, taskId = ti, completed = newPercent, rewardReceived = completed)

          case _ =>
            Logger.error("Incorrect request to makeTest")
            Some(user)
        }

        u ifSome { v =>
          OkApiResult(MakeTaskResult(v))
        }
      }
    }
  }
}

