package controllers.web.rest.component

import controllers.domain.OkApiResult

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

  def setGender() = wrapJsonApiCallReturnBody[WSSetGenderResult] { (js, r) =>
    val v = Json.read[WSSetGenderRequest](js.toString)

    val gender = Gender.withName(v.gender)

    api.setGender(SetGenderRequest(r.user, gender))
  }

  def setDebug() = wrapJsonApiCallReturnBody[WSSetDebugResult] { (js, r) =>
    val v = Json.read[WSSetDebugRequest](js.toString)

    api.setDebug(SetDebugRequest(r.user, v.debug))
  }

  def setCity() = wrapJsonApiCallReturnBody[WSSetCityResult] { (js, r) =>
    val v = Json.read[WSSetCityRequest](js.toString)

    api.setCity(SetCityRequest(r.user, v.city))
  }

  def setCountry() = wrapJsonApiCallReturnBody[WSSetCountryResult] { (js, r) =>
    val v = Json.read[WSSetCountryRequest](js.toString)

    api.setCountry(SetCountryRequest(r.user, v.country))
  }

  def getCountryList = wrapJsonApiCallReturnBody[WSGetCountryListResult] { (js, r) =>
    api.getCountryList(GetCountryListRequest(r.user))
  }

}

