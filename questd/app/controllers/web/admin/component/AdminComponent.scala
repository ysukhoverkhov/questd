package controllers.web.admin.component

import controllers.domain.libs.facebook.FacebookComponent
import controllers.domain.DomainAPIComponent


trait AdminComponent { component: DomainAPIComponent =>

  val admin: Admin 

  class Admin
    extends AdminAppImpl
    with ThemesCRUDImpl {

    // TODO IMPLEMENT! Introduce accessors here.
    val api = component.api

  }

}

