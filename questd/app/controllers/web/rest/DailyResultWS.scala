package controllers.web.rest

import controllers.web.rest.helpers.AccessToWSInstance
import play.api.mvc._

object DailyResultWS extends Controller with AccessToWSInstance {

  def getDailyResult = ws.getDailyResult

  def getRightsAtLevel = ws.getRightsAtLevel

  def getLevelsForRights = ws.getLevelsForRights
}

