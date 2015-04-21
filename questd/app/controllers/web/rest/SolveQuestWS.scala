package controllers.web.rest

import play.api.mvc._
import controllers.web.rest.component.helpers.AccessToWSInstance

object SolveQuestWS extends Controller with AccessToWSInstance {

  def solveQuest = ws.solveQuest
  def bookmarkQuest = ws.bookmarkQuest
}

