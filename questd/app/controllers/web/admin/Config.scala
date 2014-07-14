package controllers.web.admin

import play.api._
import play.api.mvc._

import controllers.web.admin.component.AdminComponent
import components.componentregistry.ComponentRegistrySingleton

object Config extends Controller {

  val admin: AdminComponent#Admin = ComponentRegistrySingleton.admin

  def config(sectionName: String) = admin.config(sectionName)
  def configUpdate(sectionName: String) = admin.configUpdate(sectionName)

}

