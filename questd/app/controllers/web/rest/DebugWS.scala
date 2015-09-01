package controllers.web.rest

import controllers.web.rest.helpers.AccessToWSInstance
import play.api.mvc._

//noinspection MutatorLikeMethodIsParameterless
object DebugWS extends Controller with AccessToWSInstance {

  def shiftDailyResult = ws.shiftDailyResult

  def test = ws.test

  def voteQuestDebug = ws.voteQuestDebug
  def voteSolutionDebug = ws.voteSolutionDebug

  def setFriendshipDebug = ws.setFriendshipDebug

  def makeBattle = ws.makeBattle

  def resetTutorial = ws.resetTutorial
  def setLevel = ws.setLevel
  def resetProfile = ws.resetProfile

  def resolveAllBattles = ws.resolveAllBattles

  def generateErrorLog = ws.generateErrorLog
}

