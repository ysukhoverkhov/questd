package controllers.web.rest.component

import controllers.domain.app.user._
import controllers.web.rest.component.helpers._
import models.domain.QuestInfoContent

private object CreateQuestWSImplTypes {

  type WSCreateQuestResult = CreateQuestResult

  case class WSCreateQuestRequest(
    media: WSContentReference,
    icon: Option[WSContentReference] = None,
    description: String)
  object WSCreateQuestRequest {
    implicit def toQuestInfoContent(v: WSCreateQuestRequest): QuestInfoContent = {
      QuestInfoContent(
        media = v.media,
        icon = v.icon.map(r => r),
        description = v.description)
    }
  }
}

trait CreateQuestWSImpl extends QuestController with SecurityWSImpl with CommonFunctions { this: WSComponent#WS =>

  import controllers.web.rest.component.CreateQuestWSImplTypes._

  def proposeQuest = wrapJsonApiCallReturnBody[WSCreateQuestResult] { (js, r) =>
    val v = Json.read[WSCreateQuestRequest](js)

    api.createQuest(CreateQuestRequest(r.user, v))
  }

}

