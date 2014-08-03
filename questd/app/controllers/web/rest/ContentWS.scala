package controllers.web.rest

import play.api._
import play.api.mvc._
import controllers.web.rest.component.helpers.AccessToWSInstance


object ContentWS extends Controller with AccessToWSInstance {

  def getQuest = ws.getQuest
  def getSolution = ws.getSolution

  def getOwnSolutions = ws.getOwnSolutions
  def getOwnQuests = ws.getOwnQuests
  
  def getPublicProfile = ws.getPublicProfile
  def getSolutionsForQuest = ws.getSolutionsForQuest
  def getSolutionsForUser = ws.getSolutionsForUser
  def getQuestsForUser = ws.getQuestsForUser

}

