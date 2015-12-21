package controllers.web.rest.component

import com.vita.scala.extensions._
import controllers.domain.OkApiResult
import controllers.domain.app.user._
import controllers.web.helpers._
import models.domain.user.profile.Functionality

import scala.language.existentials

private object DailyResultWSImplTypes {

  type WSGetDailyResultResult = GetDailyResultResult

  case class WSGetRightsAtLevelsRequest(
    levelFrom: Int,
    levelTo: Int)

  type WSGetRightsAtLevelsResult = GetRightsAtLevelsResult

  case class WSGetLevelsForRightsResult (levels: Map[String, Int])

  case class WSGetLevelsForRightsRequest(
    functionality: List[String])
}

trait DailyResultWSImpl extends BaseController with SecurityWSImpl with CommonFunctions { this: WSComponent#WS =>

  import controllers.web.rest.component.DailyResultWSImplTypes._

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

    api.getLevelsForRights(GetLevelsForRightsRequest(r.user, requestedFunctionality)) map { apiResult =>
      val rv = (apiResult.levels.keys.map(_.toString) zip apiResult.levels.values).toMap
      OkApiResult(WSGetLevelsForRightsResult(rv))
    }
  }
}

