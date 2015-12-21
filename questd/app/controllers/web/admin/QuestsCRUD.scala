package controllers.web.admin

import components.componentregistry.ComponentRegistrySingleton
import controllers.web.admin.component.AdminComponent
import play.api.mvc._

object QuestsCRUD extends Controller {

  val admin: AdminComponent#Admin = ComponentRegistrySingleton.admin

  def quests(id: String) = admin.quests.quests(id)

  def updateQuest() = admin.quests.updateQuest()

  def selectQuestStatus() = admin.quests.selectQuestStatus()
}

