package controllers.web.admin

import components.componentregistry.ComponentRegistrySingleton
import controllers.web.admin.component.AdminComponent
import play.api.mvc._
import views.html.admin.index


object TutorialScriptsCRUD extends Controller {

  val admin: AdminComponent#Admin = ComponentRegistrySingleton.admin

  def tutorial(platform: String) = admin.tutorialScripts.tutorial(platform)
  def updateAction(platform: String, elementId: String) = admin.tutorialScripts.updateAction(platform, elementId)
  def addElement(platform: String) = admin.tutorialScripts.addElement(platform)
  def deleteElement(platform: String, elementId: String) = admin.tutorialScripts.deleteElement(platform, elementId)
  def addParamToElementAction(platform: String, elementId: String) = admin.tutorialScripts.addParamToElementAction(platform, elementId)
  def deleteParamFromElementAction(platform: String, elementId: String, paramKey: String) = admin.tutorialScripts.deleteParamFromElementAction(platform, elementId, paramKey)
  def saveParamInElementAction(platform: String, elementId: String, paramKey: String) = admin.tutorialScripts.saveParamInElementAction(platform, elementId, paramKey)

  def updateCondition(platform: String, elementId: String, conditionIndex: Int) = admin.tutorialScripts.updateCondition(platform, elementId, conditionIndex)
  def addConditionToElement(platform: String, elementId: String) = admin.tutorialScripts.addConditionToElement(platform, elementId)
  def deleteConditionFromElement(platform: String, elementId: String, conditionIndex: Int) = admin.tutorialScripts.deleteConditionFromElement(platform, elementId, conditionIndex)
  def addParamToElementCondition(platform: String, elementId: String, conditionIndex: Int) = admin.tutorialScripts.addParamToElementCondition(platform, elementId, conditionIndex)
  def deleteParamFromElemCondition(platform: String, elementId: String, conditionIndex: Int, paramKey: String) = admin.tutorialScripts.deleteParamFromElemCondition(platform, elementId, conditionIndex, paramKey)
  def saveParamInElementCondition(platform: String, elementId: String, conditionIndex: Int, paramKey: String) = admin.tutorialScripts.saveParamInElementCondition(platform, elementId, conditionIndex, paramKey)

  def updateTrigger(platform: String, elementId: String, index: Int) = admin.tutorialScripts.updateTrigger(platform, elementId, index)
  def addTriggerToElement(platform: String, elementId: String) = admin.tutorialScripts.addTriggerToElement(platform, elementId)
  def deleteTriggerFromElement(platform: String, elementId: String, index: Int) = admin.tutorialScripts.deleteTriggerFromElement(platform, elementId, index)
  def addParamToElementTrigger(platform: String, elementId: String, index: Int) = admin.tutorialScripts.addParamToElementTrigger(platform, elementId, index)
  def deleteParamFromElemTrigger(platform: String, elementId: String, index: Int, paramKey: String) = admin.tutorialScripts.deleteParamFromElemTrigger(platform, elementId, index, paramKey)
  def saveParamInElementTrigger(platform: String, elementId: String, index: Int, paramKey: String) = admin.tutorialScripts.saveParamInElementTrigger(platform, elementId, index, paramKey)

  def exportTutorialScript(platform: String) = admin.tutorialScripts.exportTutorialScript(platform)
}

