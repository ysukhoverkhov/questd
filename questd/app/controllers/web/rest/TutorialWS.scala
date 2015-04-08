package controllers.web.rest

import controllers.web.rest.component.helpers.AccessToWSInstance
import play.api.mvc._

object TutorialWS extends Controller with AccessToWSInstance {

  def getTutorial = ws.getTutorial
  def getTutorialState = ws.getTutorialState
  def setTutorialState = ws.setTutorialState
  def assignTutorialTask = ws.assignTutorialTask
  def incTutorialTask = ws.incTutorialTask
}

