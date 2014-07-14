package controllers.web.rest.component

import play.api._
import play.api.mvc._
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import controllers.web.rest.component.helpers._
import controllers.web.rest.protocol._
import controllers.domain.app.user._

trait ShortlistWSImpl extends QuestController with SecurityWSImpl { this: WSComponent#WS =>


  def getShortlist = wrapApiCallReturnBody[WSGetShortlistResult] { r =>
    api.getShortlist(GetShortlistRequest(r.user))
  }
  
  def costToShortlist = wrapApiCallReturnBody[WSCostToShortlistResult] { r =>
    api.costToShortlist(CostToShortlistRequest(r.user))
  }
  
  def addToShortlist = wrapJsonApiCallReturnBody[WSAddToShortlistResult] { (js, r) =>
    val v = Json.read[WSAddToShortlistRequest](js.toString)

    api.addToShortlist(AddToShortlistRequest(r.user, v.id))
  }
  
  
  def removeFromShortlist = wrapJsonApiCallReturnBody[WSRemoveFromShortlistResult] { (js, r) =>
    val v = Json.read[WSRemoveFromShortlistRequest](js.toString)

    api.removeFromShortlist(RemoveFromShortlistRequest(r.user, v.id))
  }

}

