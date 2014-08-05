package controllers.web.rest.component

import controllers.domain.libs.facebook.FacebookComponent
import controllers.domain.DomainAPIComponent
import components._
import controllers.web.rest.config.WSConfigHolder

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
    with ShortlistWSImpl
    with FriendsWSImpl
    with MessagesWSImpl
    with MiscWSImpl
    with TutorialWSImpl

    with FBAccessor
    with APIAccessor

    with WSConfigHolder {

    val fb = component.fb
    val api = component.api
  }

}

