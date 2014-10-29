package controllers.web.rest

import play.api.mvc._
import controllers.web.rest.component.helpers.AccessToWSInstance

object VoteQuestSolutionWS extends Controller with AccessToWSInstance {

  def voteQuestSolution = ws.voteQuestSolution

}

