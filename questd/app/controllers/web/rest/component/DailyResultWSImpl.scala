package controllers.web.rest.component

import play.api._
import play.api.mvc._
import controllers.domain.app.user._
import controllers.domain._
import controllers.web.rest.component.helpers._
import controllers.web.rest.component._
import controllers.web.rest.protocol._
import models.domain._
import org.json4s._

trait DailyResultWSImpl extends QuestController with SecurityWSImpl with CommonFunctions { this: WSComponent#WS =>

  def getDailyResult = wrapApiCallReturnBody[WSGetDailyResultResult] { r =>
    api.getDailyResult(GetDailyResultRequest(r.user))
  }

  def getRightsAtLevel = wrapJsonApiCallReturnBody[WSGetRightsAtLevelsResult] { (js, r) =>
    val v = Json.read[WSGetRightsAtLevelsRequest](js)
    api.getRightsAtLevels(GetRightsAtLevelsRequest(r.user, v.levelFrom, v.levelTo))
  }

  def getLevelsForRights = wrapJsonApiCallReturnBody[WSGetLevelsForRightsResult] { (js, r) =>
    val v = Json.read[WSGetLevelsForRightsRequest](js)
    api.getLevelsForRights(GetLevelsForRightsRequest(r.user, v.functionality))
  }

  def shiftDailyResult = wrapApiCallReturnBody[WSShiftDailyResultResult] { r =>
    api.resetDailyTasks(ResetDailyTasksRequest(r.user))
    api.shiftDailyResult(ShiftDailyResultRequest(r.user))
  }
}

