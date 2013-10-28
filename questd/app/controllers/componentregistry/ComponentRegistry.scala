package controllers.componentregistry

import controllers.web.rest.component.WSComponent
import controllers.domain.libs.facebook.FacebookComponent
import models.store.DatabaseComponent
import controllers.domain.DomainAPIComponent
import models.store.mongo.MongoDatabaseComponent

trait ComponentRegistry
  extends WSComponent
  with FacebookComponent
  with MongoDatabaseComponent
  with DomainAPIComponent {

  val db: Database = new MongoDatabase
  val api = new DomainAPI
  val fb = new Facebook
  val ws = new WS

}


object ComponentRegistrySingleton extends ComponentRegistry
