package controllers.web.admin

import components.componentregistry.ComponentRegistrySingleton
import controllers.web.admin.component.AdminComponent
import play.api.mvc._


object TutorialScriptsCRUD extends Controller {

  val admin: AdminComponent#Admin = ComponentRegistrySingleton.admin

  def tutorial(platform: String) = admin.tutorialScripts.tutorial(platform)
  def addElement(platform: String) = admin.tutorialScripts.addElement(platform)
  def deleteElement(platform: String, elementId: String) = admin.tutorialScripts.deleteElement(platform, elementId)
  def upElement(platform: String, elementId: String) = admin.tutorialScripts.upElement(platform, elementId)
  def downElement(platform: String, elementId: String) = admin.tutorialScripts.downElement(platform, elementId)

  def updateAction(platform: String, elementId: String, actionIndex: Int) = admin.tutorialScripts.updateAction(platform, elementId, actionIndex)
  def addActionToElement(platform: String, elementId: String) = admin.tutorialScripts.addActionToElement(platform, elementId)
  def deleteActionFromElement(platform: String, elementId: String, actionIndex: Int) = admin.tutorialScripts.deleteActionFromElement(platform, elementId, actionIndex)
  def addParamToElementAction(platform: String, elementId: String, actionIndex: Int) = admin.tutorialScripts.addParamToElementAction(platform, elementId, actionIndex)
  def deleteParamFromElementAction(platform: String, elementId: String, actionIndex: Int, paramKey: String) = admin.tutorialScripts.deleteParamFromElementAction(platform, elementId, actionIndex, paramKey)
  def saveParamInElementAction(platform: String, elementId: String, actionIndex: Int, paramKey: String) = admin.tutorialScripts.saveParamInElementAction(platform, elementId, actionIndex, paramKey)

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
  def importTutorialScript(platform: String) = admin.tutorialScripts.importTutorialScript(platform)
}

