package controllers.web.rest

import play.api.mvc._
import controllers.web.rest.component.helpers.AccessToWSInstance

object DailyResultWS extends Controller with AccessToWSInstance {

  def getDailyResult = ws.getDailyResult

  def getRightsAtLevel = ws.getRightsAtLevel

  def getLevelsForRights = ws.getLevelsForRights
}

