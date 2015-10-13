package controllers.web.rest.component

import controllers.domain.app.misc.{GetTimeRequest, GetTimeResult}
import controllers.web.helpers._

private object MiscWSImplTypes {
  type WSGetTimeResult = GetTimeResult
}

trait MiscWSImpl extends BaseController with SecurityWSImpl { this: WSComponent#WS =>

  import MiscWSImplTypes._

  def getTime = wrapApiCallReturnBody[WSGetTimeResult] { r =>
    api.getTime(GetTimeRequest(r.user))
  }
}

