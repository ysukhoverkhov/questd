package controllers.web.admin

import components.componentregistry.ComponentRegistrySingleton
import controllers.web.admin.component.AdminComponent
import play.api.mvc._

object TutorialTasksCRUD extends Controller {

  val admin: AdminComponent#Admin = ComponentRegistrySingleton.admin

  def tutorialTasks(id: String) = admin.tutorial.tutorialTasks(id)

  def updateTutorialTask() = admin.tutorial.updateTutorialTask()
}

