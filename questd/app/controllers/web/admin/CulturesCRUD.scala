package controllers.web.admin

import play.api._
import play.api.mvc._

import controllers.web.admin.component.AdminComponent
import components.componentregistry.ComponentRegistrySingleton

object CulturesCRUD extends Controller {

  val admin: AdminComponent#Admin = ComponentRegistrySingleton.admin

  def cultures(id: String) = admin.cultures(id)
  
  def deleteCultureCB(id: String) = admin.deleteCultureCB(id)

  def createCultureCB = admin.createCultureCB

}

