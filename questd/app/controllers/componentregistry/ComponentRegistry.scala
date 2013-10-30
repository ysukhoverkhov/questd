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

  lazy val db: Database = new MongoDatabase
  lazy val api = new DomainAPI
  lazy val fb = new Facebook
  lazy val ws = new WS

}


object ComponentRegistrySingleton extends ComponentRegistry
