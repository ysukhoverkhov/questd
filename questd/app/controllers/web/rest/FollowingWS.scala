package controllers.web.rest

import play.api.mvc._
import controllers.web.rest.component.helpers.AccessToWSInstance

object FollowingWS extends Controller with AccessToWSInstance {

  def getFollowing = ws.getFollowing
  def getFollowers = ws.getFollowers
  def costToFollow = ws.costToFollow
  def addToFollowing() = ws.addToFollowing()
  def removeFromFollowing() = ws.removeFromFollowing()
  def getSuggestsForFollowing = ws.getSuggestsForFollowing
}

