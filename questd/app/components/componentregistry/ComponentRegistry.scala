package components.componentregistry

import controllers.services.devicenotifications.DeviceNotificationsComponent
import controllers.web.rest.component.WSComponent
import controllers.domain.DomainAPIComponent
import models.store.mongo.MongoDatabaseComponent
import controllers.web.admin.component.AdminComponent
import controllers.tasks.TasksComponent
import components.random.RandomComponent
import controllers.services.socialnetworks.component.SocialNetworkComponent

trait ComponentRegistry
  extends WSComponent
  with SocialNetworkComponent
  with MongoDatabaseComponent
  with DomainAPIComponent
  with AdminComponent
  with TasksComponent
  with RandomComponent
  with DeviceNotificationsComponent {

  protected lazy val rand = new Random
  protected lazy val db: Database = new MongoDatabase
  protected lazy val api = new DomainAPI
  protected lazy val sn = new SocialNetwork
  lazy val ws = new WS
  lazy val admin = new Admin
  protected lazy val tasks = new Tasks
  protected val deviceNotifications = new DeviceNotifications
}


object ComponentRegistrySingleton extends ComponentRegistry {

  // Initialize lazy value
  tasks

}
