package controllers.web.rest.component

import play.api._
import play.api.mvc._
import controllers.web.rest.component.helpers._
import controllers.web.rest.protocol._
import controllers.domain.app.user._

trait ContentWSImpl extends QuestController with SecurityWSImpl with CommonFunctions { this: WSComponent#WS =>

  def getQuest = wrapJsonApiCallReturnBody[WSGetQuestResult] { (js, r) =>
    val v = Json.read[WSGetQuestRequest](js)

    api.getQuest(GetQuestRequest(r.user, v.id))
  }

  // TODO implement me.
  def getSolution = wrapJsonApiCallReturnBody[WSGetQuestResult] { (js, r) =>
    val v = Json.read[WSGetQuestRequest](js)

    api.getQuest(GetQuestRequest(r.user, v.id))
  }

}

