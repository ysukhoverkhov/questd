package controllers.web.admin

import play.api._
import play.api.mvc._

import controllers.web.admin.component.AdminComponent
import components.componentregistry.ComponentRegistrySingleton

object ThemesCRUD extends Controller {

  val admin: AdminComponent#Admin = ComponentRegistrySingleton.admin

  def themes(id: String) = admin.themes.objects(id)
  def deleteThemeCB(id: String) = admin.themes.deleteObjectCB(id)
  def createThemeCB = admin.themes.createObjectCB

}

