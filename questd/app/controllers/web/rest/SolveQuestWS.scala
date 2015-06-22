package controllers.web.rest

import controllers.web.rest.helpers.AccessToWSInstance
import play.api.mvc._

object SolveQuestWS extends Controller with AccessToWSInstance {

  def solveQuest = ws.solveQuest
  def bookmarkQuest = ws.bookmarkQuest
}

