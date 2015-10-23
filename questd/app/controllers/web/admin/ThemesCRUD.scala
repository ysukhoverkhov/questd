package controllers.web.admin

import components.componentregistry.ComponentRegistrySingleton
import controllers.web.admin.component.AdminComponent
import play.api.mvc._

object ThemesCRUD extends Controller {

  val admin: AdminComponent#Admin = ComponentRegistrySingleton.admin

  def themes(id: String) = admin.themes.objects(id)
  def deleteThemeCB(id: String) = admin.themes.deleteObjectCB(id)
  def createThemeCB = admin.themes.createObjectCB

}

