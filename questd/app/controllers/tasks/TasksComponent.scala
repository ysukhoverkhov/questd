package controllers.tasks

import akka.actor.Props
import play.Logger
import play.api.libs.concurrent.Akka
import play.api.Play.current
import controllers.tasks.crawlers._
import components.ConfigHolder
import models.domain.config._

trait TasksComponent {

  val tasks: Tasks

  class Tasks extends ConfigHolder {

    // ConfigHolder implementation
    val configSectionName = "Tasks"
    val defaultConfiguration = ConfigSection(
      configSectionName,
      Map(("akka://application/user/DummyCrawler", "0/10 * * * * ?")))

    // Dummy task for debug
    val dummyCrawler = Akka.system.actorOf(DummyCrawler.props, name = DummyCrawler.name)

    // Creating main tasks dispatcher.
    val dispetcher = Akka.system.actorOf(TasksDispatcher.props(config), name = TasksDispatcher.name)

  }

}

