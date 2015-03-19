package controllers.web.admin.component

import controllers.domain.DomainAPIComponent


trait AdminComponent { component: DomainAPIComponent =>

  class Admin {

    val app = new AdminAppImpl(component.api)
    val users = new UsersCRUDImpl(component.api)
    val quests = new QuestsCRUDImpl(component.api)
    val solutions = new SolutionsCRUDImpl(component.api)
    val battles = new BattlesCRUDImpl(component.api)
    val themes = new ThemesCRUDImpl(component.api)
    val cultures = new CulturesCRUDImpl(component.api)
    val tutorial = new TutorialTasksCRUDImpl(component.api)
    val config = new ConfigImpl(component.api)
  }
}
