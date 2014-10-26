package controllers.web.rest

import play.api.mvc._
import controllers.web.rest.component.helpers.AccessToWSInstance

object SolveQuestWS extends Controller with AccessToWSInstance {

  def getSolveQuestCost = ws.getSolveQuestCost
  def proposeSolution = ws.proposeSolution
}

