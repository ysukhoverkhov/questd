package controllers.web.admin

import components.componentregistry.ComponentRegistrySingleton
import controllers.web.admin.component.AdminComponent
import play.api.mvc._


object Maintains extends Controller {

  val admin: AdminComponent#Admin = ComponentRegistrySingleton.admin

  def cleanup = admin.maintains.cleanup

  def resetProfiles = admin.maintains.resetProfiles

  def exportAnalytics = admin.maintains.exportAnalytics
}

