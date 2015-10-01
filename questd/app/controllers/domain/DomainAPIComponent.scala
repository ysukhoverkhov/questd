package controllers.domain

import controllers.domain.app.battle.{BattleFetchAPI, BattleAPI}
import controllers.domain.app.comment.CommentsAPI
import controllers.domain.app.theme.ThemeFetchAPI
import models.store._
import components._
import controllers.domain.app.user._
import controllers.domain.app.quest._
import controllers.domain.app.solution._
import controllers.domain.admin._
import controllers.domain.config.ApiConfigHolder
import controllers.domain.app.misc.MiscAPI
import logic.LogicBootstrapper
import components.random.RandomComponent
import controllers.domain.app.quest.QuestsFetchAPI
import controllers.services.socialnetworks.component.SocialNetworkComponent

trait DomainAPIComponent { component: DatabaseComponent with RandomComponent with SocialNetworkComponent =>

  protected val api: DomainAPI

  class DomainAPI
    extends DBAccessor
    with APIAccessor
    with RandomAccessor
    with SNAccessor

    with AuthAPI
    with ProfileAPI
    with UserAPI
    with DailyResultAPI
    with StatsAPI
    with CreateQuestAPI
    with SolveQuestAPI
    with FightBattleAPI
    with VoteQuestAPI
    with VoteSolutionAPI
    with VoteBattleAPI
    with ContentAPI
    with FollowingAPI
    with FriendsAPI
    with BanAPI
    with EventsAPI
    with CommentsAPI
    with ConversationsAPI
    with ChallengesAPI
    with MiscAPI
    with TasksAPI
    with TutorialAPI
    with TimeLineAPI
    with DebugAPI

    with QuestAPI
    with QuestsFetchAPI
    with ThemeFetchAPI
    with SolutionAPI
    with SolutionFetchAPI
    with BattleAPI
    with BattleFetchAPI

    with ThemesAdminAPI
    with UsersAdminAPI
    with QuestsAdminAPI
    with TutorialTasksAdminAPI
    with SolutionsAdminAPI
    with BattlesAdminAPI
    with CulturesAdminAPI
    with ConfigAdminAPI
    with MaintenanceAdminAPI

    with ApiConfigHolder

    with LogicBootstrapper {

    lazy val sn = component.sn
    lazy val db = component.db
    lazy val api = component.api // This is lazy since it references to his parent which creates us during initialization.
    lazy val rand = component.rand

    // TODO: put here reference to component of device notifications.
  }
}

