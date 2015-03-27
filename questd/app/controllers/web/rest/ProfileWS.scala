package controllers.web.rest

import play.api.mvc._
import controllers.web.rest.component.helpers.AccessToWSInstance


object ProfileWS extends Controller with AccessToWSInstance {

  def getProfile = ws.getProfile

  def getStats = ws.getStats

  def setGender() = ws.setGender()

  def setDebug() = ws.setDebug()

  def setCity() = ws.setCity()

  def setCountry() = ws.setCountry()

  def getCountryList = ws.getCountryList
}

