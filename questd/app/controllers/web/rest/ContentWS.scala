package controllers.web.rest

import play.api.mvc._
import controllers.web.rest.component.helpers.AccessToWSInstance


object ContentWS extends Controller with AccessToWSInstance {

  def getQuest = ws.getQuest
  def getSolution = ws.getSolution
  def getBattle = ws.getBattle

  def getOwnSolutions = ws.getOwnSolutions
  def getOwnQuests = ws.getOwnQuests

  def getPublicProfiles = ws.getPublicProfiles
  def getSolutionsForQuest = ws.getSolutionsForQuest
  def getSolutionsForUser = ws.getSolutionsForUser
  def getQuestsForUser = ws.getQuestsForUser

}

