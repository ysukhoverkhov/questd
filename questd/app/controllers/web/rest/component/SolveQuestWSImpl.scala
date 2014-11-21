package controllers.web.rest.component

import controllers.domain.app.user._
import controllers.web.rest.component.helpers._
import controllers.web.rest.protocol._
import models.domain.{ContentType, ContentReference, SolutionInfoContent}

trait SolveQuestWSImpl extends QuestController with SecurityWSImpl with CommonFunctions { this: WSComponent#WS =>

  def solveQuest = wrapJsonApiCallReturnBody[WSSolveQuestResult] { (js, r) =>
    import scala.language.implicitConversions

    // TODO: find similar function elsewhere and remove code dup. perhaps move it to WSContentReference (move others as well.)

    implicit def toContentReference(v: WSContentReference): ContentReference = {
      ContentReference(
        contentType = ContentType.withName(v.contentType),
        storage = v.storage,
        reference = v.reference
      )
    }
    implicit def toQuestInfoContent(v: WSSolveQuestRequest): SolutionInfoContent = {
      SolutionInfoContent(
        media = v.media,
        icon = v.icon.map(r => r))
    }

    val v = Json.read[WSSolveQuestRequest](js.toString)

    api.solveQuest(SolveQuestRequest(r.user, v.questId, v))
  }
}

