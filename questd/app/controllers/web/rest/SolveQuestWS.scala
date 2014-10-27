package controllers.web.rest

import play.api.mvc._
import controllers.web.rest.component.helpers.AccessToWSInstance

object SolveQuestWS extends Controller with AccessToWSInstance {

  def proposeSolution = ws.proposeSolution
}

