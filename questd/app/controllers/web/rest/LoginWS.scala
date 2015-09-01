package controllers.web.rest

import controllers.web.rest.helpers.AccessToWSInstance
import play.api.mvc._


object LoginWS extends Controller with AccessToWSInstance {

  def login = ws.login

}

