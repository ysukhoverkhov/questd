package controllers.web.rest

import controllers.web.rest.helpers.AccessToWSInstance
import play.api.mvc._


//noinspection EmptyParenMethodAccessedAsParameterless,MutatorLikeMethodIsParameterless
object ChallengesWS extends Controller with AccessToWSInstance {

  def challengeBattle = ws.challengeBattle
  def getBattleRequests = ws.getBattleRequests
  def respondBattleRequest = ws.respondBattleRequest
}

