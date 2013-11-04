package controllers.tasks

import akka.actor.Props

import play.Logger
import play.api.libs.concurrent.Akka
import play.api.Play.current

import controllers.tasks.crawlers._

trait TasksComponent {
  
  val tasks: Tasks
  
  class Tasks {

    // Creating main tasks dispatcher.
    val dummyCrawler = Akka.system.actorOf(DummyCrawler.props, name = DummyCrawler.name)
    
    val dispetcher = Akka.system.actorOf(TasksDispatcher.props, name = TasksDispatcher.name)
    
  }

}

