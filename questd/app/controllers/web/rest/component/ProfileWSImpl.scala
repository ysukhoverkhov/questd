package controllers.web.rest.component

import controllers.domain.app.user._
import controllers.web.rest.component.helpers._
import models.domain.{Gender, Profile}

private object ProfileWSImplTypes {

  /**
   * Get profile response
   */
  type WSProfileResult = Profile

  /**
   * Set Gender protocol.
   */
  type WSSetGenderResult = SetGenderResult
  case class WSSetGenderRequest(gender: String)

  /**
   * Set debug protocol.
   */
  type WSSetDebugResult = SetDebugResult
  case class WSSetDebugRequest(debug: String)

  /**
   * Set city protocol.
   */
  type WSSetCityResult = SetCityResult
  case class WSSetCityRequest(city: String)

  /**
   * Set country protocol
   */
  type WSGetCountryListResult = GetCountryListResult
  type WSSetCountryResult = SetCountryResult
  case class WSSetCountryRequest(country: String)

}

trait ProfileWSImpl extends QuestController with SecurityWSImpl { this: WSComponent#WS =>

  import controllers.web.rest.component.ProfileWSImplTypes._

  def getProfile = wrapReturnAny { implicit request =>
    request.user.profile
  }

  def getStats = wrapReturnAny { implicit request =>
    request.user.stats
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

  def getCountryList = wrapApiCallReturnBody[WSGetCountryListResult] { (r) =>
    api.getCountryList(GetCountryListRequest(r.user))
  }

}

