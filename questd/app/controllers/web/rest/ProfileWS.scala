package controllers.web.rest

import play.api._
import play.api.mvc._

import controllers.web.rest.component.WSComponent
import controllers.componentregistry.ComponentRegistrySingleton


object ProfileWS extends Controller {

  val wsimpl: WSComponent#WS = ComponentRegistrySingleton.ws

  def getName = wsimpl.getName

}

