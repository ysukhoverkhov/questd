package controllers.domain

import models.store._
import components._
import controllers.domain.app.user._
import controllers.domain.app.quest._
import controllers.domain.app.questsolution._
import controllers.domain.admin._
import controllers.domain.app.questsolution.QuestSolutionAPI

trait DomainAPIComponent { component: DatabaseComponent =>

  val api: DomainAPI

  class DomainAPI
    extends DBAccessor

    with AuthAPI
    with ProfileAPI
    with DailyResultAPI
    with StatsAPI
    with ProposeQuestAPI
    with SolveQuestAPI
    with VoteQuestProposalAPI
    with VoteQuestSolutionAPI
    with ContentAPI
    
    with QuestAPI
    with QuestSolutionAPI

    with ThemesAdminAPI
    with QuestsAdminAPI
    with ConfigAdminAPI {

    // db for out traits
    val db = component.db
  }

}

