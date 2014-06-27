package controllers.web.rest

import play.api._
import play.api.mvc._
import controllers.web.rest.component.helpers.AccessToWSInstance

object TutorialWS extends Controller with AccessToWSInstance {

  def getTutorialState = TODO
  def setTutorialState = TODO
  def assignTutorialTask = TODO
  def setTutorialTaskProgress = TODO
}

