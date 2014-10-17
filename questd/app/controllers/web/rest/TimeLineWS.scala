package controllers.web.rest

import play.api.mvc._
import controllers.web.rest.component.helpers.AccessToWSInstance

object TimeLineWS extends Controller with AccessToWSInstance {

  def getTimeLine = ws.getTimeLine
}

