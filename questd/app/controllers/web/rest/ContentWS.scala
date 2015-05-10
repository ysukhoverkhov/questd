package controllers.web.rest

import controllers.web.rest.helpers.AccessToWSInstance
import play.api.mvc._


object ContentWS extends Controller with AccessToWSInstance {

  def getPublicProfiles = ws.getPublicProfiles
  def getQuests = ws.getQuests
  def getSolutions = ws.getSolution
  def getBattles = ws.getBattle

  def getQuestsForUser = ws.getQuestsForUser

  def getSolutionsForUser = ws.getSolutionsForUser
  def getSolutionsForQuest = ws.getSolutionsForQuest

  def getBattlesForUser = ws.getBattlesForUser
  def getBattlesForSolution = ws.getBattlesForSolution
}

