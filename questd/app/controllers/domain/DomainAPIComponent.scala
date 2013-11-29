package controllers.domain

import models.store._
import components._
import controllers.domain.user._
import controllers.domain.admin._

trait DomainAPIComponent { component: DatabaseComponent =>

  val api: DomainAPI

  class DomainAPI
    extends DBAccessor
    with AuthAPI
    with ProfileAPI
    with ProposeQuestAPI
    with SolveQuestAPI
    with VoteQuestProposalAPI
    
    with ThemesAdminAPI 
    with QuestsAdminAPI
    with ConfigAdminAPI {

    // db for out traits
    val db = component.db

  }

}

