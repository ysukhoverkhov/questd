package controllers.web.rest

import controllers.web.rest.helpers.AccessToWSInstance
import play.api.mvc._

object VoteSolutionWS extends Controller with AccessToWSInstance {

  def voteSolution = ws.voteSolution

}

