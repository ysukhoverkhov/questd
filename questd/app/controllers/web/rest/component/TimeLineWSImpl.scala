package controllers.web.rest.component

import controllers.domain.app.user._
import controllers.web.rest.component.helpers._

private object TimeLineWSImplTypes {

  type WSGetTimeLineResult = GetTimeLineResult
  case class WSGetTimeLineRequest (
    // Number of page in result, zero based.
    pageNumber: Int,

    // Number of items on a page.
    pageSize: Int,

    // Id of an item to stop getting results at
    untilEntryId: Option[String] = None)

}

trait TimeLineWSImpl extends QuestController with SecurityWSImpl with CommonFunctions { this: WSComponent#WS =>

  import controllers.web.rest.component.TimeLineWSImplTypes._

  def getTimeLine = wrapJsonApiCallReturnBody[WSGetTimeLineResult] { (js, r) =>

    val v = Json.read[WSGetTimeLineRequest](js.toString)

    api.getTimeLine(GetTimeLineRequest(
      r.user,
      v.pageNumber,
      v.pageSize,
      v.untilEntryId))
  }
}

