package controllers.web.rest.component

import controllers.domain.libs.facebook.FacebookComponent
import controllers.domain.DomainAPIComponent
import components._

trait WSComponent { component: DomainAPIComponent with FacebookComponent =>

  val ws: WS

  class WS
    extends LoginWSImpl
    with ProfileWSImpl
    with ProposeQuestWSImpl
    with SolveQuestWSImpl
    with VoteQuestWSImpl
    
    with FBAccessor
    with APIAccessor {

    val fb = component.fb
    val api = component.api

  }

}

