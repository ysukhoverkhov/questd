package controllers.web.admin

import play.api._
import play.api.mvc._

import controllers.web.admin.component.AdminComponent
import components.componentregistry.ComponentRegistrySingleton

object SolutionsCRUD extends Controller {

  val admin: AdminComponent#Admin = ComponentRegistrySingleton.admin

  def solutions(id: String) = admin.solutions(id)

}

