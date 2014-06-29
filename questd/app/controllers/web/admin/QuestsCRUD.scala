package controllers.web.admin

import play.api._
import play.api.mvc._

import controllers.web.admin.component.AdminComponent
import components.componentregistry.ComponentRegistrySingleton

object QuestsCRUD extends Controller {

  val admin: AdminComponent#Admin = ComponentRegistrySingleton.admin

  def entries(id: String) = admin.quests(id)

  def update = admin.updateQuest
}

