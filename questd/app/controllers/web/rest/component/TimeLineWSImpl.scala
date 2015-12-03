package controllers.web.rest.component

import com.vita.scala.extensions._
import controllers.domain.app.user._
import controllers.web.helpers._
import models.domain.user.timeline.TimeLineType

private object TimeLineWSImplTypes {

  /**
    * @param pageNumber   Number of page in result, zero based.
    * @param pageSize     Number of items on a page.
    * @param objectType   Type of object to return (TimeLineType), all if not specified.
    * @param untilEntryId Id of an item to stop getting results at
    */
  case class WSGetTimeLineRequest (
    pageNumber: Int,
    pageSize: Int,
    objectType: Option[String] = None,
    untilEntryId: Option[String] = None)
  type WSGetTimeLineResult = GetTimeLineResult

  /**
   * @param entryId Id of entry to hide in timeline.
   */
  case class WSHideFromTimeLineRequest (
    entryId: String)
  type WSHideFromTimeLineResult = HideFromTimeLineResult
}

//noinspection MutatorLikeMethodIsParameterless
trait TimeLineWSImpl extends BaseController with SecurityWSImpl with CommonFunctions { this: WSComponent#WS =>

  import controllers.web.rest.component.TimeLineWSImplTypes._

  def getTimeLine = wrapJsonApiCallReturnBody[WSGetTimeLineResult] { (js, r) =>
    val v = Json.read[WSGetTimeLineRequest](js.toString)

    api.getTimeLine(GetTimeLineRequest(
      r.user,
      v.pageNumber,
      v.pageSize,
      v.objectType.map(TimeLineType.withNameEx),
      v.untilEntryId))
  }

  def hideFromTimeLine = wrapJsonApiCallReturnBody[WSHideFromTimeLineResult] { (js, r) =>
    val v = Json.read[WSHideFromTimeLineRequest](js.toString)

    api.hideFromTimeLine(HideFromTimeLineRequest(
      r.user,
      v.entryId))
  }
}

