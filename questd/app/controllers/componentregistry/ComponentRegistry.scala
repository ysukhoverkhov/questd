package controllers.componentregistry

import controllers.web.rest.component.WSComponent
import controllers.domain.libs.facebook.FacebookComponent
import models.store.DatabaseComponent
import controllers.domain.DomainAPIComponent
import models.store.mongo.MongoDatabaseComponent
import controllers.web.admin.component.AdminComponent

trait ComponentRegistry
  extends WSComponent
  with FacebookComponent
  with MongoDatabaseComponent
  with DomainAPIComponent 
  with AdminComponent {

  lazy val db: Database = new MongoDatabase
  lazy val api = new DomainAPI
  lazy val fb = new Facebook
  lazy val ws = new WS
  lazy val admin = new Admin

}


object ComponentRegistrySingleton extends ComponentRegistry
