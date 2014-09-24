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

trait DebugWSImpl extends QuestController with SecurityWSImpl with CommonFunctions { this: WSComponent#WS =>

  case class WSTestResult(r: String)
  
  def test = wrapApiCallReturnBody[WSTestResult] { r =>
    
    //      shiftStats(ShiftStatsRequest(user))
//import controllers.domain.app.quest._
//	  calculateProposalThresholds(CalculateProposalThresholdsRequest(10, 3))
//      shiftHistory(ShiftHistoryRequest(user))

    OkApiResult(WSTestResult("lalai"))
  }
}

