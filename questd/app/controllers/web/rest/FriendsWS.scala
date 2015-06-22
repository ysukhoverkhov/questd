package controllers.web.rest

import controllers.web.rest.helpers.AccessToWSInstance
import play.api.mvc._


object FriendsWS extends Controller with AccessToWSInstance {

  def getFriends = ws.getFriends
  def costToRequestFriendship = ws.costToRequestFriendship
  def askFriendship = ws.askFriendship
  def respondFriendship = ws.respondFriendship

  //noinspection EmptyParenMethodAccessedAsParameterless,MutatorLikeMethodIsParameterless
  def removeFromFriends = ws.removeFromFriends
}

