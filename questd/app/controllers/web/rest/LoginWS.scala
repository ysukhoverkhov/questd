package controllers.web.rest

import play.api._
import play.api.mvc._
import controllers.web.rest.component.helpers.AccessToWSInstance



object LoginWS extends Controller with AccessToWSInstance {

  def login = ws.login

}

