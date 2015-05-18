package controllers.web.rest.component

import controllers.domain.DomainAPIComponent
import components._
import controllers.web.rest.config.WSConfigHolder
import controllers.sn.component.SocialNetworkComponent

trait WSComponent { component: DomainAPIComponent with SocialNetworkComponent =>

  val ws: WS

  class WS
    extends LoginWSImpl
    with ProfileWSImpl
    with CreateQuestWSImpl
    with SolveQuestWSImpl
    with VoteQuestWSImpl
    with VoteQuestSolutionWSImpl
    with DailyResultWSImpl
    with TimeLineWSImpl
    with ContentWSImpl
    with FollowingWSImpl
    with FriendsWSImpl
    with MessagesWSImpl
    with MiscWSImpl
    with TutorialWSImpl
    with CommentsWSImpl
    with UploadWSImpl
    with DebugWSImpl

    with SNAccessor
    with APIAccessor

    with WSConfigHolder {

    val sn = component.sn
    val api = component.api
  }

}

