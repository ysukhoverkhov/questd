package controllers.web.rest.component.helpers

import controllers.web.rest.component.WSComponent
import components.componentregistry.ComponentRegistrySingleton

trait AccessToWSInstance {
  val ws: WSComponent#WS = ComponentRegistrySingleton.ws

}