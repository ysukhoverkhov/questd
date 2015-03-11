package controllers.web.rest.component

import controllers.domain.app.user._
import controllers.web.rest.component.helpers._

private object TutorialWSImplTypes {

  case class WSGetTutorialStateRequest(
    /// Id of a platform to get state for.
    platformId: String)

  /// if no state for platform present empty result will be returned.
  type WSGetTutorialStateResult = GetTutorialStateResult

  case class WSSetTutorialStateRequest(
    /// Id of a platform to get state for.
    platformId: String,
    state: String)

  /// may return LimitExceeded in "allowed" field if there are too many platforms (logic.constants.NumberOfStoredTutorialPlatforms)
  /// or state is too long (logic.constants.MaxLengthOfTutorlaPlatformState).
  type WSSetTutorialStateResult = SetTutorialStateResult

  case class WSAssignTutorialTaskRequest(
    taskId: String)

  /// LimitExceeded if task was already requested.
  /// OutOfContent if task with this id is not exists.
  type WSAssignTutorialTaskResult = AssignTutorialTaskResult

  case class WSIncTutorialTaskRequest(
    taskId: String)

  /// OutOfContent if the task is not in active tasks.
  type WSIncTutorialTaskResult = IncTutorialTaskResult
}

trait TutorialWSImpl extends QuestController with SecurityWSImpl { this: WSComponent#WS =>

  import controllers.web.rest.component.TutorialWSImplTypes._

  def getTutorialState = wrapJsonApiCallReturnBody[WSGetTutorialStateResult] { (js, r) =>
    val v = Json.read[WSGetTutorialStateRequest](js)

    api.getTutorialState(GetTutorialStateRequest(r.user, v.platformId))
  }

  def setTutorialState() = wrapJsonApiCallReturnBody[WSSetTutorialStateResult] { (js, r) =>
    val v = Json.read[WSSetTutorialStateRequest](js)

    api.setTutorialState(SetTutorialStateRequest(r.user, v.platformId, v.state))
  }

  def assignTutorialTask = wrapJsonApiCallReturnBody[WSAssignTutorialTaskResult] { (js, r) =>
    val v = Json.read[WSAssignTutorialTaskRequest](js)

    api.assignTutorialTask(AssignTutorialTaskRequest(r.user, v.taskId))
  }

  def incTutorialTask = wrapJsonApiCallReturnBody[WSIncTutorialTaskResult] { (js, r) =>
    val v = Json.read[WSIncTutorialTaskRequest](js)

    api.incTutorialTask(IncTutorialTaskRequest(r.user, v.taskId))
  }

}

