package controllers.web.rest.component

import play.api._
import play.api.mvc._
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import controllers.web.rest.component.helpers._
import controllers.web.rest.protocol._

trait ShortlistWSImpl extends QuestController with SecurityWSImpl { this: WSComponent#WS =>

//  def getProfile = Authenticated.async { implicit request =>
//    Future {
//      Ok(Json.write[WSProfileResult](request.user.profile)).as(JSON) 
//    }
//  }

  def getShortlist = TODO
  def costToShortlist = TODO
  def addToShortlist = TODO
  def removeFromShortlist = TODO

}

