package controllers.web.rest.component

import controllers.domain.app.user._
import controllers.web.helpers._
import models.domain.solution.SolutionInfoContent

private object SolveQuestWSImplTypes {

  import scala.language.implicitConversions

  case class WSSolveQuestRequest(
    questId: String,
    media: Option[WSContentReference] = None,
    icon: Option[WSContentReference] = None,
    description: Option[String] = None)
  object WSSolveQuestRequest {
    implicit def toSolutionInfoContent(v: WSSolveQuestRequest): SolutionInfoContent = {
      SolutionInfoContent(
        media = v.media.map(r => r),
        icon = v.icon.map(r => r),
        description = v.description)
    }
  }

  type WSSolveQuestResult = SolveQuestResult

  case class WSBookmarkQuestRequest(
    questId: String)

  type WSBookmarkQuestResult = BookmarkQuestResult

}

trait SolveQuestWSImpl extends BaseController with SecurityWSImpl with CommonFunctions { this: WSComponent#WS =>

  import controllers.web.rest.component.SolveQuestWSImplTypes._

  def solveQuest = wrapJsonApiCallReturnBody[WSSolveQuestResult] { (js, r) =>
    val v = Json.read[WSSolveQuestRequest](js.toString)

    api.solveQuest(SolveQuestRequest(r.user, v.questId, v))
  }

  def bookmarkQuest = wrapJsonApiCallReturnBody[WSBookmarkQuestResult] { (js, r) =>
    val v = Json.read[WSBookmarkQuestRequest](js.toString)

    api.bookmarkQuest(BookmarkQuestRequest(r.user, v.questId))
  }
}

