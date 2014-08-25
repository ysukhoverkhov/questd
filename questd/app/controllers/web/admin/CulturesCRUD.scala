package controllers.web.admin

import play.api._
import play.api.mvc._

import controllers.web.admin.component.AdminComponent
import components.componentregistry.ComponentRegistrySingleton

object CulturesCRUD extends Controller {

  val admin: AdminComponent#Admin = ComponentRegistrySingleton.admin

  def cultures(id: String) = admin.cultures(id)
  
  // TODO: uncomment me.
//  def deleteThemeCB(id: String) = admin.deleteThemeCB(id)
//  def createThemeCB = admin.createThemeCB

}

