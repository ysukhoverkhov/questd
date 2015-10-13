package controllers.web.rest.component

import components._
import controllers.domain.DomainAPIComponent
import controllers.services.socialnetworks.component.SocialNetworkComponent
import controllers.web.rest.config.WSConfigHolder

trait WSComponent { component: DomainAPIComponent with SocialNetworkComponent =>

  val ws: WS

  class WS
    extends LoginWSImpl
    with ProfileWSImpl
    with CreateQuestWSImpl
    with SolveQuestWSImpl
    with VoteQuestWSImpl
    with VoteSolutionWSImpl
    with VoteBattleWSImpl
    with DailyResultWSImpl
    with TimeLineWSImpl
    with ContentWSImpl
    with FollowingWSImpl
    with FriendsWSImpl
    with BanWSImpl
    with EventsWSImpl
    with MiscWSImpl
    with TutorialWSImpl
    with CommentsWSImpl
    with ConversationsWSImpl
    with ChallengesWSImpl
    with AnalyticsWSImpl
    with UploadWSImpl
    with DebugWSImpl

    with SNAccessor
    with APIAccessor

    with WSConfigHolder {

    val sn = component.sn
    val api = component.api
  }

}

