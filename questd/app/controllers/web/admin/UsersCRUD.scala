package controllers.web.admin

import components.componentregistry.ComponentRegistrySingleton
import controllers.web.admin.component.AdminComponent
import play.api.mvc._

object UsersCRUD extends Controller {

  val admin: AdminComponent#Admin = ComponentRegistrySingleton.admin

  def users(id: String) = admin.users.users(id)
}
