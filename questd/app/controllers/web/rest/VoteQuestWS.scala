package controllers.web.rest

import play.api.mvc._
import controllers.web.rest.component.helpers.AccessToWSInstance

object VoteQuestWS extends Controller with AccessToWSInstance {

  def voteQuest = ws.voteQuest

}

