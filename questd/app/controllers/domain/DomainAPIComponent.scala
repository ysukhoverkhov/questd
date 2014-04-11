package controllers.domain

import models.store._
import components._
import controllers.domain.app.user._
import controllers.domain.app.quest._
import controllers.domain.app.questsolution._
import controllers.domain.admin._
import controllers.domain.app.questsolution.QuestSolutionAPI
import controllers.domain.config.ApiConfigHolder
import controllers.domain.app.misc.MiscAPI

trait DomainAPIComponent { component: DatabaseComponent =>

  val api: DomainAPI

  class DomainAPI
    extends DBAccessor
    with APIAccessor

    with AuthAPI
    with ProfileAPI
    with DailyResultAPI
    with StatsAPI
    with ProposeQuestAPI
    with SolveQuestAPI
    with VoteQuestProposalAPI
    with VoteQuestSolutionAPI
    with ContentAPI
    with ShortlistAPI
    with FriendsAPI
    with MessagesAPI 
    with MiscAPI
    
    with QuestAPI
    with QuestSolutionAPI

    with ThemesAdminAPI
    with QuestsAdminAPI
    with ConfigAdminAPI 
    
    with ApiConfigHolder { 

    val db = component.db
    lazy val api = component.api // This is lazy since it references to his parent which creates us during initialization.
  }

}

