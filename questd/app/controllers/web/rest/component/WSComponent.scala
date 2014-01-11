package controllers.web.rest.component

import controllers.domain.libs.facebook.FacebookComponent
import controllers.domain.DomainAPIComponent
import components._
import models.domain.admin.ConfigSection

trait WSComponent { component: DomainAPIComponent with FacebookComponent =>

  val ws: WS

  class WS
    extends LoginWSImpl
    with ProfileWSImpl
    with ProposeQuestWSImpl
    with SolveQuestWSImpl
    with VoteQuestProposalWSImpl
    with VoteQuestSolutionWSImpl
    with DailyResultWSImpl
    with ContentWSImpl

    with FBAccessor
    with APIAccessor

    with ConfigHolder {

    val fb = component.fb
    val api = component.api

    
    val configSectionName = "Web Service"
    val defaultConfiguration = ConfigSection(
      configSectionName,
      Map(("Min App Version", "1")))
  }

}

