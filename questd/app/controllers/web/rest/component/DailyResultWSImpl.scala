package controllers.web.rest.component

import controllers.domain.OkApiResult
import controllers.domain.app.user._
import controllers.web.rest.component.helpers._
import controllers.web.rest.protocol._
import models.domain.Functionality
import _root_.helpers.rich._
import scala.language.existentials

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

    val requestedFunctionality = v.functionality.map(Functionality.withNameEx)

    api.getLevelsForRights(GetLevelsForRightsRequest(r.user, requestedFunctionality)) ifOk { apiResult =>
      val rv = (apiResult.levels.keys.map(_.toString) zip apiResult.levels.values).toMap
      OkApiResult(WSGetLevelsForRightsResult(rv))
    }
  }

  def shiftDailyResult = wrapApiCallReturnBody[WSShiftDailyResultResult] { r =>
    api.resetDailyTasks(ResetDailyTasksRequest(r.user))
    api.populateTimeLineWithRandomThings(PopulateTimeLineWithRandomThingsRequest(r.user))
    api.shiftDailyResult(ShiftDailyResultRequest(r.user))
  }
}

