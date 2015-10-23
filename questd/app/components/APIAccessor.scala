package components

import controllers.domain._

trait APIAccessor {
  val api: DomainAPIComponent#DomainAPI
}

