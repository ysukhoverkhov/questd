package controllers.web.rest

import controllers.web.rest.helpers.AccessToWSInstance
import play.api.mvc._

object TimeLineWS extends Controller with AccessToWSInstance {

  def getTimeLine = ws.getTimeLine
  def hideFromTimeLine = ws.hideFromTimeLine
}

