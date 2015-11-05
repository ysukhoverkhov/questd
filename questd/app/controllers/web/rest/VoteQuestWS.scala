package controllers.web.rest

import controllers.web.rest.helpers.AccessToWSInstance
import play.api.mvc._

object VoteQuestWS extends Controller with AccessToWSInstance {

  def voteQuest = ws.voteQuest

  def hideOwnQuest = ws.hideOwnQuest
}

