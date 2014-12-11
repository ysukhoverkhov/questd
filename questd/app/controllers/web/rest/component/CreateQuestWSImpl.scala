package controllers.web.rest.component

import controllers.domain.app.user._
import controllers.web.rest.component.helpers._
import controllers.web.rest.protocol._


trait CreateQuestWSImpl extends QuestController with SecurityWSImpl with CommonFunctions { this: WSComponent#WS =>

  def proposeQuest = wrapJsonApiCallReturnBody[WSCreateQuestResult] { (js, r) =>
    val v = Json.read[WSCreateQuestRequest](js)

    api.createQuest(CreateQuestRequest(r.user, v))
  }

}

