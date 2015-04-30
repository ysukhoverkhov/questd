package controllers.web.rest

import play.api.mvc._
import controllers.web.rest.component.helpers.AccessToWSInstance

object CreateQuestWS extends Controller with AccessToWSInstance {

  def createQuest = ws.proposeQuest

}

