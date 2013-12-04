package controllers.tasks

import akka.actor.Props
import play.Logger
import play.api.libs.concurrent.Akka
import play.api.Play.current
import controllers.tasks.crawlers._
import controllers.domain.DomainAPIComponent
import components._
import models.domain._
import models.domain.admin._

trait TasksComponent { component: DomainAPIComponent =>

  val tasks: Tasks

  class Tasks
    extends APIAccessor
    with ConfigHolder {

    val api = component.api

    // ConfigHolder implementation
    val configSectionName = "Tasks"
    val defaultConfiguration = ConfigSection(
      configSectionName,
      Map(("akka://application/user/UsersHourlyCrawler", "0 0 0/1 * * ?"),
          ("akka://application/user/UsersWeeklyCrawler", "0 0 5 ? * MON"))) // TODO check it.

    val usersHourlyCrawler = Akka.system.actorOf(UsersHourlyCrawler.props(api), name = UsersHourlyCrawler.name)
    val usersWeeklyCrawler = Akka.system.actorOf(UsersWeeklyCrawler.props(api), name = UsersWeeklyCrawler.name)

    // Creating main tasks dispatcher.
    val dispetcher = Akka.system.actorOf(TasksDispatcher.props(config), name = TasksDispatcher.name)

  }

}

