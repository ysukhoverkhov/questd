package controllers.web.rest.component

import controllers.domain.app.user._
import controllers.web.rest.component.helpers._
import controllers.web.rest.protocol._

trait SolveQuestWSImpl extends QuestController with SecurityWSImpl with CommonFunctions { this: WSComponent#WS =>

  def solveQuest = wrapJsonApiCallReturnBody[WSSolveQuestResult] { (js, r) =>
    val v = Json.read[WSSolveQuestRequest](js.toString)

    api.solveQuest(SolveQuestRequest(r.user, v.questId, v))
  }

  def bookmarkQuest = wrapJsonApiCallReturnBody[WSBookmarkQuestResult] { (js, r) =>
    val v = Json.read[WSBookmarkQuestRequest](js.toString)

    api.bookmarkQuest(BookmarkQuestRequest(r.user, v.questId))
  }
}

