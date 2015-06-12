package controllers.web.admin

import components.componentregistry.ComponentRegistrySingleton
import controllers.web.admin.component.AdminComponent
import play.api.mvc._


object Messages extends Controller {

  val admin: AdminComponent#Admin = ComponentRegistrySingleton.admin

  def compose(sendResult: String) = admin.messages.compose(sendResult)
  def send = admin.messages.send
}

