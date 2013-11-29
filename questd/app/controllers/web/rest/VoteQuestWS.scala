package controllers.web.rest

import play.api._
import play.api.mvc._
import controllers.web.rest.component.helpers.AccessToWSInstance

object VoteQuestWS extends Controller with AccessToWSInstance {
  
  def getQuestToVote = ws.getQuestToVote
  def voteQuest = ws.voteQuest

}

