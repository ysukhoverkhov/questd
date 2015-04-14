package controllers.web.rest

import play.api._
import play.api.mvc._
import controllers.web.rest.component.helpers.AccessToWSInstance


object MiscWS extends Controller with AccessToWSInstance {

  def getTime = ws.getTime
}

