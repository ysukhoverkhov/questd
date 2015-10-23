package controllers.web.admin

import components.componentregistry.ComponentRegistrySingleton
import controllers.web.admin.component.AdminComponent
import play.api.mvc._


object CulturesCRUD extends Controller {

  val admin: AdminComponent#Admin = ComponentRegistrySingleton.admin

  def cultures(id: String) = admin.cultures.objects(id)

  def deleteCultureCB(id: String) = admin.cultures.deleteObjectCB(id)

  def createCultureCB = admin.cultures.createObjectCB

  def extractCountry(country: String) = admin.cultures.extractCountry(country)

}

