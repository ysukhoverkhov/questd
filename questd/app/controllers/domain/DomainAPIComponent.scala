package controllers.domain

import models.store._


trait DomainAPIComponent { component: DatabaseComponent => 

  val api: DomainAPI
  
  class DomainAPI extends AuthAPI with ProfileAPI {

    // db for out traits
    val db = component.db
    
  }
  
}

