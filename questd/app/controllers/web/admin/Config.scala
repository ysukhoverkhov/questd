package controllers.web.admin

import controllers.web.admin.component.AdminComponent
import components.componentregistry.ComponentRegistrySingleton
import play.api.mvc._


object Config extends Controller {

  val admin: AdminComponent#Admin = ComponentRegistrySingleton.admin

  def config(sectionName: String) = admin.config.config(sectionName)
  def configUpdate(sectionName: String) = admin.config.configUpdate(sectionName)

}

