package controllers.web.rest

import play.api._
import play.api.mvc._
import controllers.web.rest.component.helpers.AccessToWSInstance


object ShortlistWS extends Controller with AccessToWSInstance {

  def getShortlist = ws.getShortlist
  def costToShortlist = ws.costToShortlist
  def addToShortlist = ws.addToShortlist
  def removeFromShortlist = ws.removeFromShortlist

}

