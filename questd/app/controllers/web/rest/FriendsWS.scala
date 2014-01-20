package controllers.web.rest

import play.api._
import play.api.mvc._
import controllers.web.rest.component.helpers.AccessToWSInstance


object FriendsWS extends Controller with AccessToWSInstance {

  def getFriends = ws.getFriends
  def costToRequestFriendship = ws.costToRequestFriendship
  def askFriendship = ws.askFriendship
  def respondFriendsip = ws.respondFriendsip
  def removeFromFriends = ws.removeFromFriends
}

