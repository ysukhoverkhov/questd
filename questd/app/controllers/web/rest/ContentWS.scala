package controllers.web.rest

import play.api.mvc._
import controllers.web.rest.component.helpers.AccessToWSInstance


object ContentWS extends Controller with AccessToWSInstance {

  def getPublicProfiles = ws.getPublicProfiles
  def getQuests = ws.getQuests
  def getSolutions = ws.getSolution
  def getBattles = ws.getBattle

  def getOwnQuests = ws.getOwnQuests
  def getOwnSolutions = ws.getOwnSolutions
  def getOwnBattles = ws.getOwnBattles

  def getQuestsForUser = ws.getQuestsForUser

  def getSolutionsForUser = ws.getSolutionsForUser
  def getSolutionsForQuest = ws.getSolutionsForQuest

  def getBattlesForUser = ws.getBattlesForUser
  def getBattlesForSolution = ws.getBattlesForSolution
}

