package controllers.tasks

import components._
import components.random.RandomComponent
import controllers.domain.DomainAPIComponent
import controllers.tasks.config.TasksConfigHolder
import controllers.tasks.crawlers.schedulers._
import play.api.Play.current
import play.api.libs.concurrent.Akka

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
    val solutionsHourlyCrawler = Akka.system.actorOf(SolutionsHourlyCrawler.props(api, rand), name = SolutionsHourlyCrawler.name)
    val solutionsWeeklyCrawler = Akka.system.actorOf(SolutionsWeeklyCrawler.props(api, rand), name = SolutionsWeeklyCrawler.name)
    val battlesHourlyCrawler = Akka.system.actorOf(BattlesHourlyCrawler.props(api, rand), name = BattlesHourlyCrawler.name)
    val challengesHourlyCrawler = Akka.system.actorOf(ChallengesHourlyCrawler.props(api, rand), name = ChallengesHourlyCrawler.name)

    // Creating main tasks dispatcher.
    val dispatcher = Akka.system.actorOf(TasksDispatcher.props(config), name = TasksDispatcher.name)

  }
}

