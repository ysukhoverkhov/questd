package controllers.web.rest

import controllers.web.rest.component.helpers.AccessToWSInstance
import play.api.mvc._


object MessagesWS extends Controller with AccessToWSInstance {

  def removeMessage = ws.removeMessage
}

