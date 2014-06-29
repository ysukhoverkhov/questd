package controllers.web.admin

import play.api._
import play.api.mvc._

import controllers.web.admin.component.AdminComponent
import components.componentregistry.ComponentRegistrySingleton

object TutorialTasksCRUD extends Controller {

  val admin: AdminComponent#Admin = ComponentRegistrySingleton.admin

  def tutorialTasks(id: String) = admin.tutorialTasks(id)

  def updateTutorialTask = admin.updateTutorialTask
}

