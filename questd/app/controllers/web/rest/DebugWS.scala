package controllers.web.rest

import controllers.web.rest.helpers.AccessToWSInstance
import play.api.mvc._

object DebugWS extends Controller with AccessToWSInstance {

  def shiftDailyResult = ws.shiftDailyResult

  def test = ws.test

  def voteQuestDebug = ws.voteQuestDebug
  def voteSolutionDebug = ws.voteSolutionDebug

  //noinspection MutatorLikeMethodIsParameterless
  def setFriendshipDebug = ws.setFriendshipDebug

  def makeBattle = ws.makeBattle
}

