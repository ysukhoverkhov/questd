package controllers.web.rest

import controllers.web.rest.helpers.AccessToWSInstance
import play.api.mvc._

object CreateQuestWS extends Controller with AccessToWSInstance {

  def createQuest = ws.createQuest

}

