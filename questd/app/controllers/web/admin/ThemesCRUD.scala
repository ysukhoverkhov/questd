package controllers.web.admin

import play.api._
import play.api.mvc._

import controllers.web.admin.component.AdminComponent
import components.componentregistry.ComponentRegistrySingleton

object ThemesCRUD extends Controller {

  val admin: AdminComponent#Admin = ComponentRegistrySingleton.admin

  def index = admin.index
  def themes(id: String) = admin.themes(id)
  def deleteThemeCB(id: String) = admin.deleteThemeCB(id)
  def createThemeCB = admin.createThemeCB

}

