package controllers.web.admin

import components.componentregistry.ComponentRegistrySingleton
import controllers.web.admin.component.AdminComponent
import play.api.mvc._


object TutorialScriptsCRUD extends Controller {

  val admin: AdminComponent#Admin = ComponentRegistrySingleton.admin

  def tutorial(platform: String) = admin.tutorialScripts.tutorial(platform)
  def updateAction(platform: String, elementId: String) = admin.tutorialScripts.updateAction(platform, elementId)
  def addElement(platform: String) = admin.tutorialScripts.addElement(platform)
  def deleteElement(platform: String, elementId: String) = admin.tutorialScripts.deleteElement(platform, elementId)
  def addParamToElementAction(platform: String, elementId: String) = admin.tutorialScripts.addParamToElementAction(platform, elementId)
  def deleteParamFromElementAction(platform: String, elementId: String, paramKey: String) = admin.tutorialScripts.deleteParamFromElementAction(platform, elementId, paramKey)
  def saveParamInElementAction(platform: String, elementId: String, paramKey: String) = admin.tutorialScripts.saveParamInElementAction(platform, elementId, paramKey)

  def addConditionToElement(platform: String, elementId: String) = admin.tutorialScripts.addConditionToElement(platform, elementId)
  def deleteConditionFromElement(platform: String, elementId: String, conditionIndex: Int) = admin.tutorialScripts.deleteConditionFromElement(platform, elementId, conditionIndex)
  def updateCondition(platform: String, elementId: String, conditionIndex: Int) = admin.tutorialScripts.updateCondition(platform, elementId, conditionIndex)
  def addParamToElementCondition(platform: String, elementId: String, conditionIndex: Int) = admin.tutorialScripts.addParamToElementCondition(platform, elementId, conditionIndex)
  def deleteParamFromElemCondition(platform: String, elementId: String, conditionIndex: Int, paramKey: String) = admin.tutorialScripts.deleteParamFromElemCondition(platform, elementId, conditionIndex, paramKey)
  def saveParamInElementCondition(platform: String, elementId: String, conditionIndex: Int, paramKey: String) = admin.tutorialScripts.saveParamInElementCondition(platform, elementId, conditionIndex, paramKey)

}

