package controllers.web.rest.component

import controllers.web.rest.component.helpers._
import controllers.web.rest.protocol._
import controllers.domain.app.user._

trait TutorialWSImpl extends QuestController with SecurityWSImpl { this: WSComponent#WS =>

  def getTutorialState = wrapJsonApiCallReturnBody[WSGetTutorialStateResult] { (js, r) =>
    val v = Json.read[WSGetTutorialStateRequest](js)

    api.getTutorialState(GetTutorialStateRequest(r.user, v.platformId))
  }

  def setTutorialState = wrapJsonApiCallReturnBody[WSSetTutorialStateResult] { (js, r) =>
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

