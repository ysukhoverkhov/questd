package controllers.web.rest

import controllers.web.rest.component.helpers.AccessToWSInstance
import play.api.mvc._

object DebugWS extends Controller with AccessToWSInstance {

  def shiftDailyResult = ws.shiftDailyResult

  def test = ws.test

  def voteQuestDebug = ws.voteQuestDebug
  def voteSolutionDebug = ws.voteSolutionDebug

  //noinspection MutatorLikeMethodIsParameterless
  def setFriendshipDebug = ws.setFriendshipDebug

}

