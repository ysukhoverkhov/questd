package controllers.web.rest.component

import controllers.domain.app.user._
import controllers.web.rest.component.helpers._
import controllers.web.rest.protocol._
import models.domain._


trait CreateQuestWSImpl extends QuestController with SecurityWSImpl with CommonFunctions { this: WSComponent#WS =>

  def proposeQuest = wrapJsonApiCallReturnBody[WSCreateQuestResult] { (js, r) =>
    import scala.language.implicitConversions
    implicit def toContentReference(v: WSContentReference): ContentReference = {
      ContentReference(
        contentType = ContentType.withName(v.contentType),
        storage = v.storage,
        reference = v.reference
      )
    }
    implicit def toQuestInfoContent(v: WSCreateQuestRequest): QuestInfoContent = {
      QuestInfoContent(
        media = v.media,
        icon = v.icon.map(r => r),
        description = v.description)
    }

    val v = Json.read[WSCreateQuestRequest](js)

    api.createQuest(CreateQuestRequest(r.user, v))
  }

}

