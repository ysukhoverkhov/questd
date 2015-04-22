package controllers.web.rest.component

import controllers.domain.app.user._
import controllers.web.rest.component.helpers._
import models.domain.SolutionInfoContent

private object SolveQuestWSImplTypes {

  import scala.language.implicitConversions

  case class WSSolveQuestRequest(
    questId: String,
    media: WSContentReference,
    icon: Option[WSContentReference] = None)
  object WSSolveQuestRequest {
    implicit def toSolutionInfoContent(v: WSSolveQuestRequest): SolutionInfoContent = {
      SolutionInfoContent(
        media = v.media,
        icon = v.icon.map(r => r))
    }
  }

  type WSSolveQuestResult = SolveQuestResult

  case class WSBookmarkQuestRequest(
    questId: String)

  type WSBookmarkQuestResult = BookmarkQuestResult

}

trait SolveQuestWSImpl extends QuestController with SecurityWSImpl with CommonFunctions { this: WSComponent#WS =>

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

