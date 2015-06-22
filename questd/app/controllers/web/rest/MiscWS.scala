package controllers.web.rest

import controllers.web.rest.helpers.AccessToWSInstance
import play.api.mvc._


object MiscWS extends Controller with AccessToWSInstance {

  def getTime = ws.getTime
}

