package controllers.web.rest

import play.api._
import play.api.mvc._

import controllers.web.rest.component.WSComponent
import components.componentregistry.ComponentRegistrySingleton



object LoginWS extends Controller {
  
  val ws: WSComponent#WS = ComponentRegistrySingleton.ws

  def loginfb = ws.loginfb

}

