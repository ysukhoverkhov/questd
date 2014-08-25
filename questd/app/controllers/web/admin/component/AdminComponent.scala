package controllers.web.admin.component

import controllers.domain.DomainAPIComponent
import components._


trait AdminComponent { component: DomainAPIComponent =>

  val admin: Admin 

  class Admin
    extends AdminAppImpl
    with ThemesCRUDImpl 
    with UsersCRUDImpl
    with QuestsCRUDImpl
    with TutorialTasksCRUDImpl
    with SolutionsCRUDImpl
    with CulturesCRUDImpl
    with ConfigImpl
    with APIAccessor {

    val api = component.api

  }

}

