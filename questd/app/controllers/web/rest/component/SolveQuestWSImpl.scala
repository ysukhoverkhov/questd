package controllers.web.rest.component

import controllers.domain.app.user._
import controllers.web.rest.component.helpers._
import controllers.web.rest.protocol._

trait SolveQuestWSImpl extends QuestController with SecurityWSImpl with CommonFunctions { this: WSComponent#WS =>

  def proposeSolution = wrapJsonApiCallReturnBody[WSProposeSolutionResult] { (js, r) =>
    val v = Json.read[WSProposeSolutionRequest](js.toString)

    api.proposeSolution(ProposeSolutionRequest(r.user, v.questId, v.solutionContent))
  }
}

