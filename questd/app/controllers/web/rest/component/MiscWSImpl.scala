package controllers.web.rest.component

import controllers.web.rest.component.helpers._
import controllers.web.rest.protocol._
import controllers.domain.app.misc.GetTimeRequest

trait MiscWSImpl extends QuestController with SecurityWSImpl { this: WSComponent#WS =>

  def getTime = wrapApiCallReturnBody[WSGetTimeResult] { r =>
    api.getTime(GetTimeRequest(r.user))
  }
}

