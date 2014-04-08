package components.componentregistry

import controllers.web.rest.component.WSComponent
import controllers.domain.libs.facebook.FacebookComponent
import models.store.DatabaseComponent
import controllers.domain.DomainAPIComponent
import models.store.mongo.MongoDatabaseComponent
import controllers.web.admin.component.AdminComponent
import controllers.tasks.TasksComponent

trait ComponentRegistry
  extends WSComponent
  with FacebookComponent
  with MongoDatabaseComponent
  with DomainAPIComponent 
  with AdminComponent 
  with TasksComponent {

  lazy val db: Database = new MongoDatabase
  lazy val api = new DomainAPI
  lazy val fb = new Facebook
  lazy val ws = new WS
  lazy val admin = new Admin
  lazy val tasks = new Tasks
}


object ComponentRegistrySingleton extends ComponentRegistry {

  // Initialize lazy value
  tasks

}
