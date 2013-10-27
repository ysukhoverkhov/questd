package controllers.web.rest.component

import controllers.domain.libs.facebook.FacebookComponent
import controllers.domain.DomainAPIComponent

trait WSComponent { component: DomainAPIComponent with FacebookComponent =>

  val ws: WS

  class WS
    extends LoginWSImpl
    with ProfileWSImpl {

    val fb = component.fb
    val api = component.api

  }

}

