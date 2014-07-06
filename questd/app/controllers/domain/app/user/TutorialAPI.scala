package controllers.domain.app.user

import models.domain._
import models.store._
import controllers.domain.DomainAPIComponent
import components._
import controllers.domain._
import controllers.domain.helpers._
import logic._
import play.Logger
import controllers.domain.app.protocol.ProfileModificationResult._

case class GetTutorialStateRequest(user: User, platformId: String)
case class GetTutorialStateResult(state: Option[String])

case class SetTutorialStateRequest(user: User, platformId: String, state: String)
case class SetTutorialStateResult(allowed: ProfileModificationResult)

case class AssignTutorialTaskRequest(user: User, taskId: String)
case class AssignTutorialTaskResult(allowed: ProfileModificationResult)

private[domain] trait TutorialAPI { this: DomainAPIComponent#DomainAPI with DBAccessor =>

  /**
   * Get state of tutorial for a specified platform.
   */
  def getTutorialState(request: GetTutorialStateRequest): ApiResult[GetTutorialStateResult] = handleDbException {
    import request._

    OkApiResult(GetTutorialStateResult(user.tutorial.clientTutorialState.get(platformId)))
  }

  /**
   * Set state of tutorial for a specified platform.
   */
  def setTutorialState(request: SetTutorialStateRequest): ApiResult[SetTutorialStateResult] = handleDbException {
    import request._

    if (user.tutorial.clientTutorialState.size > logic.constants.NumberOfStoredTutorialPlatforms ||
      state.length > logic.constants.MaxLengthOfTutorialPlatformState) {
      OkApiResult(SetTutorialStateResult(LimitExceeded))
    } else {
      db.user.setTutorialState(user.id, platformId, state)
      OkApiResult(SetTutorialStateResult(OK))
    }

  }

  /**
   * Assign client task to user.
   */
  def assignTutorialTask(request: AssignTutorialTaskRequest): ApiResult[AssignTutorialTaskResult] = handleDbException {
    import request._
    // TODO test me.
    // 1. check is the task was already given.
    if (user.tutorial.assignedTutorialTaskIds.contains(taskId)) {
      OkApiResult(AssignTutorialTaskResult(LimitExceeded))
    } else {
      db.tutorialTask.readById(taskId) match {
        case Some(t) => {
          // 2. If rewardReceived is true remove all tasks and give current one as a solely task.
          if (user.profile.dailyTasks.rewardReceived == true) {
            db.user.resetTasks(
              user.id,
              DailyTasks(tasks = List(), reward = Assets()),
              user.getResetTasksTimeout)
          }

          {
            // 3. Add task to list of assigned tutorial tasks.
            db.user.addTutorialTaskAssigned(user.id, taskId)
          } ifSome { v =>
            // 4. Add reward of current task to reward for current daily tasks and increase timeout to infinity.
            val reward = t.reward
            val taskToAdd = t.task

            db.user.addTasks(
              user.id,
              List(taskToAdd),
              reward)

            OkApiResult(AssignTutorialTaskResult(OK))
          }
        }
        case None => {
          OkApiResult(AssignTutorialTaskResult(OutOfContent))
        }
      }
    }
  }
}

