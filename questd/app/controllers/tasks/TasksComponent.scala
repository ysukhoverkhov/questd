package controllers.tasks

import play.Logger
import akka.actor.Props
import play.api.libs.concurrent.Akka
import play.api.Play.current

trait TasksComponent {
  
  val tasks: Tasks
  
  class Tasks {

    // Creating main tasks dispatcher.
    
    val dispetcher = Akka.system.actorOf(TasksDispatcher.props)
    
    
  }

}

