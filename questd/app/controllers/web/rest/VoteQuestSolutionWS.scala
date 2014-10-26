package controllers.web.rest

import play.api._
import play.api.mvc._
import controllers.web.rest.component.helpers.AccessToWSInstance

object VoteQuestSolutionWS extends Controller with AccessToWSInstance {

//  def getQuestSolutionToVote = ws.getQuestSolutionToVote
  def voteQuestSolution = ws.voteQuestSolution

}

