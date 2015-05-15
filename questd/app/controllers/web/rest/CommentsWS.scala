package controllers.web.rest

import controllers.web.rest.helpers.AccessToWSInstance
import play.api.mvc._


//noinspection EmptyParenMethodAccessedAsParameterless,MutatorLikeMethodIsParameterless
object CommentsWS extends Controller with AccessToWSInstance {

  def postComment = ws.postComment
  def getCommentsForObject = ws.getCommentsForObject
}

