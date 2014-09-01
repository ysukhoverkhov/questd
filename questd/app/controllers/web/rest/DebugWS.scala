package controllers.web.rest

import play.api._
import play.api.mvc._
import controllers.web.rest.component.helpers.AccessToWSInstance

object DebugWS extends Controller with AccessToWSInstance {
  
  def test = ws.test
}

