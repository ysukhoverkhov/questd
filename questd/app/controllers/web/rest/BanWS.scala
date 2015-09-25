package controllers.web.rest

import controllers.web.rest.helpers.AccessToWSInstance
import play.api.mvc._


object BanWS extends Controller with AccessToWSInstance {

  def banUser = ws.banUser
  def unbanUser = ws.unbanUser
  def getBannedUsers = ws.getBannedUsers
}

