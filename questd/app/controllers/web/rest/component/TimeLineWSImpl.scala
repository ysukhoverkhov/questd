package controllers.web.rest.component

import controllers.domain.app.user._
import controllers.web.rest.component.helpers._
import controllers.web.rest.protocol._

trait TimeLineWSImpl extends QuestController with SecurityWSImpl with CommonFunctions { this: WSComponent#WS =>

  def getTimeLine = wrapJsonApiCallReturnBody[WSGetTimeLineResult] { (js, r) =>

    val v = Json.read[WSGetTimeLineRequest](js.toString)

    api.getTimeLine(GetTimeLineRequest(
      r.user,
      v.pageNumber,
      v.pageSize))
  }
}

