package controllers.web.rest

import play.api._
import play.api.mvc._
import controllers.web.rest.component.helpers.AccessToWSInstance


object MessagesWS extends Controller with AccessToWSInstance {

  def getMessages = ws.getMessages
  def removeMessage = ws.removeMessage
}

