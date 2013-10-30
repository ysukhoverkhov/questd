package controllers.web.admin

import play.api._
import play.api.mvc._

import controllers.web.admin.component.AdminComponent
import controllers.componentregistry.ComponentRegistrySingleton

object ThemesCRUD extends Controller {

  val admin: AdminComponent#Admin = ComponentRegistrySingleton.admin

  def index = admin.index
  def themes = admin.themes
  def deleteThemeCB = admin.deleteThemeCB
  def createThemeCB = admin.createThemeCB
  def editTheme = admin.editTheme
  def editThemeCB = admin.editThemeCB

}

