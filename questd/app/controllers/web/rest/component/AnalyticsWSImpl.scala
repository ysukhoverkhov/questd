package controllers.web.rest.component

import controllers.domain.app.user._
import controllers.web.helpers._


private object AnalyticsWSImplTypes {

  /**
   * Set Gender protocol.
   */
  type WSSetUserSourceResult = SetUserSourceResult
  case class WSSetUserSourceRequest(userSource: String)
}

//noinspection MutatorLikeMethodIsParameterless
trait AnalyticsWSImpl extends BaseController with SecurityWSImpl { this: WSComponent#WS =>

  import controllers.web.rest.component.AnalyticsWSImplTypes._

  def setUserSource = wrapJsonApiCallReturnBody[WSSetUserSourceResult] { (js, r) =>
    val v = Json.read[WSSetUserSourceRequest](js.toString)

    api.setUserSource(SetUserSourceRequest(r.user, v.userSource))
  }
}

