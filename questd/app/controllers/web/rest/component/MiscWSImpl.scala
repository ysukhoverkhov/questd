package controllers.web.rest.component

import controllers.web.helpers._
import controllers.domain.app.misc.{GetTimeResult, GetTimeRequest}

private object MiscWSImplTypes {
  type WSGetTimeResult = GetTimeResult
}

trait MiscWSImpl extends QuestController with SecurityWSImpl { this: WSComponent#WS =>

  import MiscWSImplTypes._

  def getTime = wrapApiCallReturnBody[WSGetTimeResult] { r =>
    api.getTime(GetTimeRequest(r.user))
  }
}

