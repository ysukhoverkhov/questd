package controllers.web.rest

import controllers.web.rest.helpers.AccessToWSInstance
import play.api.mvc._


//noinspection EmptyParenMethodAccessedAsParameterless,MutatorLikeMethodIsParameterless
object ConversationsWS extends Controller with AccessToWSInstance {

  def createConversation = ws.createConversation
  def getMyConversations = ws.getMyConversations
}

