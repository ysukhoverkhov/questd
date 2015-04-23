package controllers.domain.app.user

import models.domain._
import controllers.domain.DomainAPIComponent
import components._
import controllers.domain._
import controllers.domain.helpers._
import controllers.domain.app.protocol.ProfileModificationResult._

case class GetCommonTutorialRequest(platform: TutorialPlatform.Value)
case class GetCommonTutorialResult(tutorialElements: List[TutorialElement])

case class GetTutorialRequest(user: User, platform: TutorialPlatform.Value)
case class GetTutorialResult(tutorialElements: List[TutorialElement])

case class GetTutorialElementsStateRequest(user: User, platform: TutorialPlatform.Value)
case class GetTutorialElementsStateResult(state: TutorialState)

case class CloseTutorialElementRequest(user: User, platform: TutorialPlatform.Value, elementId: String)
case class CloseTutorialElementResult(allowed: ProfileModificationResult, state: Option[TutorialState] = None)

case class AssignTutorialTaskRequest(user: User, platform: TutorialPlatform.Value, taskId: String)
case class AssignTutorialTaskResult(allowed: ProfileModificationResult, profile: Option[Profile] = None)

case class IncTutorialTaskRequest(user: User, taskId: String)
case class IncTutorialTaskResult(allowed: ProfileModificationResult, profile: Option[Profile] = None)

private[domain] trait TutorialAPI { this: DomainAPIComponent#DomainAPI with DBAccessor =>

  /**
   * Get common for all users tutorial for the platform.
   */
  def getCommonTutorial(request: GetCommonTutorialRequest): ApiResult[GetCommonTutorialResult] = handleDbException {
    val commonScenario: List[TutorialElement] =
      db.tutorial.readById(request.platform.toString) map {_.elements} getOrElse List.empty

    OkApiResult(GetCommonTutorialResult(commonScenario))
  }

  /**
   * Get actual for current user tutorial.
   */
  def getTutorial(request: GetTutorialRequest): ApiResult[GetTutorialResult] = handleDbException {
    val commonScenario: List[TutorialElement] =
      db.tutorial.readById(request.platform.toString) map {_.elements} getOrElse List.empty

    OkApiResult(GetTutorialResult(commonScenario))
  }

  /**
   * Get state of tutorial for a specified platform.
   */
  def getTutorialElementsState(request: GetTutorialElementsStateRequest): ApiResult[GetTutorialElementsStateResult] = handleDbException {
    import request._

    OkApiResult(GetTutorialElementsStateResult(user.tutorialStates(platform.toString)))
  }

  /**
   * Set state of tutorial for a specified platform.
   */
  def closeTutorialElement(request: CloseTutorialElementRequest): ApiResult[CloseTutorialElementResult] = handleDbException {
    import request._

    db.user.addClosedTutorialElement(user.id, platform.toString, elementId) ifSome { v =>
      OkApiResult(CloseTutorialElementResult(OK, Some(v.tutorialStates(platform.toString))))
    }
  }

  /**
   * Assign client task to user.
   */
  def assignTutorialTask(request: AssignTutorialTaskRequest): ApiResult[AssignTutorialTaskResult] = handleDbException {
    import request._
    // 1. check is the task was already given.
    if (user.tutorialStates(platform.toString).assignedTutorialTaskIds.contains(taskId)) {
      OkApiResult(AssignTutorialTaskResult(LimitExceeded))
    } else {
      db.tutorialTask.readById(taskId) match {
        case Some(t) =>
          // 2. If rewardReceived is true remove all tasks and give current one as a solely task.
          if (user.profile.dailyTasks.rewardReceived) {
            db.user.resetTasks(
              user.id,
              DailyTasks(tasks = List.empty, reward = Assets()),
              user.getResetTasksTimeout)
          }

          {
            // 3. Add task to list of assigned tutorial tasks.
            db.user.addTutorialTaskAssigned(id = user.id, platform = platform.toString, taskId = taskId)
          } ifSome { v =>
            // 4. Add reward of current task to reward for current daily tasks and increase timeout to infinity.
            val taskToAdd = t.task

            db.user.addTasks(
              user.id,
              List(taskToAdd)) ifSome { v =>
                OkApiResult(AssignTutorialTaskResult(OK, Some(v.profile)))
              }
          }
        case None =>
          OkApiResult(AssignTutorialTaskResult(OutOfContent))
      }
    }
  }

  /**
   * Inc by one progress of tutorial task.
   */
  def incTutorialTask(request: IncTutorialTaskRequest): ApiResult[IncTutorialTaskResult] = handleDbException {
    import request._

    user.profile.dailyTasks.tasks.find(t => t.tutorialTaskId == Some(taskId)).fold[ApiResult[IncTutorialTaskResult]] {
      OkApiResult(IncTutorialTaskResult(OutOfContent))
    }
    { t: Task =>
      {
        makeTask(MakeTaskRequest(user = user, taskId = Some(t.id)))
      } map { r =>
        OkApiResult(IncTutorialTaskResult(OK, Some(r.user.profile)))
      }
    }
  }
}

