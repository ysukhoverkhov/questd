package controllers.web.rest

import controllers.web.rest.helpers.AccessToWSInstance
import play.api.mvc._

object VoteBattleWS extends Controller with AccessToWSInstance {

  def voteBattle= ws.voteBattle

}

