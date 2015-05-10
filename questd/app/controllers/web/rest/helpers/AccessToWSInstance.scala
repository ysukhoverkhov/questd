package controllers.web.rest.helpers

import components.componentregistry.ComponentRegistrySingleton
import controllers.web.rest.component.WSComponent

trait AccessToWSInstance {
  val ws: WSComponent#WS = ComponentRegistrySingleton.ws

}
