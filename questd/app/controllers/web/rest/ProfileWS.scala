package controllers.web.rest

import play.api._
import play.api.mvc._
import controllers.web.rest.component.helpers.AccessToWSInstance


object ProfileWS extends Controller with AccessToWSInstance {

  def getProfile = ws.getProfile

  def setGender = ws.setGender
  
  def setDebug = ws.setDebug
}

