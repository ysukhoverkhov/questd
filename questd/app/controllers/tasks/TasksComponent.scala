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
import controllers.tasks.config.TasksConfigHolder
import components.random.RandomComponent

trait TasksComponent { component: DomainAPIComponent with RandomComponent =>

  protected val tasks: Tasks

  class Tasks
    extends APIAccessor
    with RandomAccessor
    with TasksConfigHolder {

    val api = component.api
    val rand = component.rand

    val usersHourlyCrawler = Akka.system.actorOf(UsersHourlyCrawler.props(api, rand), name = UsersHourlyCrawler.name)
    val usersWeeklyCrawler = Akka.system.actorOf(UsersWeeklyCrawler.props(api, rand), name = UsersWeeklyCrawler.name)

    // Creating main tasks dispatcher.
    val dispetcher = Akka.system.actorOf(TasksDispatcher.props(config), name = TasksDispatcher.name)

  }

}

