package controllers.web.rest

import controllers.web.rest.helpers.AccessToWSInstance
import play.api.mvc._

object TutorialWS extends Controller with AccessToWSInstance {

  def getTutorial = ws.getTutorial
  def closeTutorialElement = ws.closeTutorialElement
  def assignTutorialTask = ws.assignTutorialTask
  def incTutorialTask = ws.incTutorialTask
}

