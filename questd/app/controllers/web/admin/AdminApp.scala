package controllers.web.admin

import controllers.web.admin.component.AdminComponent
import components.componentregistry.ComponentRegistrySingleton
import play.api.mvc._


object AdminApp extends Controller {

  val admin: AdminComponent#Admin = ComponentRegistrySingleton.admin

  def index = admin.app.index

  def login = admin.app.login
}

