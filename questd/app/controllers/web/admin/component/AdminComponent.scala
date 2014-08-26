package controllers.web.admin.component

import controllers.domain.DomainAPIComponent
import components._


trait AdminComponent { component: DomainAPIComponent =>

  val admin: Admin 

  // TODO: move everything to vals.
  class Admin
    extends AdminAppImpl
//    with ThemesCRUDImpl 
    with UsersCRUDImpl
    with QuestsCRUDImpl
    with TutorialTasksCRUDImpl
    with SolutionsCRUDImpl
    with CulturesCRUDImpl
    with ConfigImpl
    with APIAccessor {

    // TODO: remove me.
    val api = component.api

    val themes = new ThemesCRUDImpl(component.api)
  }

}

