package controllers.web.admin

import play.api._
import play.api.mvc._

import controllers.web.admin.component.AdminComponent
import components.componentregistry.ComponentRegistrySingleton

object AdminApp extends Controller {

  val admin: AdminComponent#Admin = ComponentRegistrySingleton.admin

  def index = admin.index
}

