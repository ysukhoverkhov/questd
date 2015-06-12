package controllers.web.rest

import controllers.web.rest.helpers.AccessToWSInstance
import play.api.mvc._


//noinspection EmptyParenMethodAccessedAsParameterless,MutatorLikeMethodIsParameterless
object MessagesWS extends Controller with AccessToWSInstance {

  def removeMessage = ws.removeMessage
}

