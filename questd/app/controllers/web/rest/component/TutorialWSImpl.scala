package controllers.web.rest.component

import controllers.domain.app.user._
import controllers.web.helpers._
import com.vita.scala.extensions._
import models.domain.common.ClientPlatform

private object TutorialWSImplTypes {

  case class WSGetTutorialRequest(
    /// Platform to get state for.
    platform: String)

  /// Get current actual tutorial for the user.
  type WSGetTutorialResult = GetTutorialResult


  case class WSCloseTutorialElementRequest(
    /// Platform to get state for.
    platform: String,
    elementId: String)

  /// may return LimitExceeded in "allowed" field if there are too many platforms (logic.constants.NumberOfStoredTutorialPlatforms)
  /// or state is too long (logic.constants.MaxLengthOfTutorlaPlatformState).
  type WSCloseTutorialElementResult = CloseTutorialElementResult

  case class WSAssignTutorialTaskRequest(
    platform: String,
    taskId: String)

  /// LimitExceeded if task was already requested.
  /// OutOfContent if task with this id is not exists.
  type WSAssignTutorialTaskResult = AssignTutorialTaskResult

  case class WSIncTutorialTaskRequest(
    taskId: String)

  /// OutOfContent if the task is not in active tasks.
  type WSIncTutorialTaskResult = IncTutorialTaskResult


  case class WSAssignTutorialQuestRequest(
    platform: String,
    questId: String)

  type WSAssignTutorialQuestResult = AssignTutorialQuestResult

}

trait TutorialWSImpl extends QuestController with SecurityWSImpl { this: WSComponent#WS =>

  import controllers.web.rest.component.TutorialWSImplTypes._

  def getTutorial = wrapJsonApiCallReturnBody[WSGetTutorialResult] { (js, r) =>
    val v = Json.read[WSGetTutorialRequest](js)

    api.getTutorial(GetTutorialRequest(r.user, ClientPlatform.withNameEx(v.platform)))
  }

  def closeTutorialElement = wrapJsonApiCallReturnBody[WSCloseTutorialElementResult] { (js, r) =>
    val v = Json.read[WSCloseTutorialElementRequest](js)

    api.closeTutorialElement(CloseTutorialElementRequest(
      user = r.user,
      platform = ClientPlatform.withNameEx(v.platform),
      elementId = v.elementId))
  }

  def assignTutorialTask = wrapJsonApiCallReturnBody[WSAssignTutorialTaskResult] { (js, r) =>
    val v = Json.read[WSAssignTutorialTaskRequest](js)

    api.assignTutorialTask(AssignTutorialTaskRequest(r.user, ClientPlatform.withNameEx(v.platform), v.taskId))
  }

  def incTutorialTask = wrapJsonApiCallReturnBody[WSIncTutorialTaskResult] { (js, r) =>
    val v = Json.read[WSIncTutorialTaskRequest](js)

    api.incTutorialTask(IncTutorialTaskRequest(r.user, v.taskId))
  }

  def assignTutorialQuest = wrapJsonApiCallReturnBody[WSAssignTutorialQuestResult] { (js, r) =>
    val v = Json.read[WSAssignTutorialQuestRequest](js)

    api.assignTutorialQuest(AssignTutorialQuestRequest(r.user, ClientPlatform.withNameEx(v.platform), v.questId))
  }
}

