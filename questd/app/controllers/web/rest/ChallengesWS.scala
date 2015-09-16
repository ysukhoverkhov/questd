package controllers.web.rest

import controllers.web.rest.helpers.AccessToWSInstance
import play.api.mvc._


//noinspection EmptyParenMethodAccessedAsParameterless,MutatorLikeMethodIsParameterless
object ChallengesWS extends Controller with AccessToWSInstance {

  def makeChallenge = ws.makeChallenge
  def getChallenge = ws.getChallenge
  def getMyChallenges = ws.getMyChallenges
  def getChallengesToMe = ws.getChallengesToMe
  def respondChallenge = ws.respondChallenge
}

