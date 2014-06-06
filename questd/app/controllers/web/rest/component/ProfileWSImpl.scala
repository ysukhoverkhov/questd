package controllers.web.rest.component

import play.api._
import play.api.mvc._
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import controllers.web.rest.component.helpers._
import controllers.web.rest.protocol._
import models.domain.Gender
import controllers.domain.app.user._

trait ProfileWSImpl extends QuestController with SecurityWSImpl { this: WSComponent#WS =>

  def getProfile = Authenticated.async { implicit request =>
    Future {
      Ok(Json.write[WSProfileResult](request.user.profile)).as(JSON) 
    }
  }
  
  def setGender = wrapJsonApiCallReturnBody[WSSetGenderResult] { (js, r) =>
    val v = Json.read[WSSetGenderRequest](js.toString)

    val gender = Gender.withName(v.gender)
    
    api.setGender(SetGenderRequest(r.user, gender))
  }
  
  def setDebug = wrapJsonApiCallReturnBody[WSSetDebugResult] { (js, r) =>
    val v = Json.read[WSSetDebugRequest](js.toString)

    api.setDebug(SetDebugRequest(r.user, v.debug))
  }
}

