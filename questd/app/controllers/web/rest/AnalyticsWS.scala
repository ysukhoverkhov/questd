package controllers.web.rest

import controllers.web.rest.helpers.AccessToWSInstance
import play.api.mvc._


//noinspection MutatorLikeMethodIsParameterless
object AnalyticsWS extends Controller with AccessToWSInstance {

  def setUserSource = ws.setUserSource
}

