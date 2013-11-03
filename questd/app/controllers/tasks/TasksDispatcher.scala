package controllers.tasks

import akka.actor.Actor
import akka.actor.Props
import akka.event.Logging
import play.Logger

object TasksDispatcher {
  def props = Props(classOf[TasksDispatcher], "TasksDispatcher")
}

class TasksDispatcher(name: String) extends Actor {

  override def preStart(): Unit = {
    // TODO initialize everything here.
    // TODO read configuration and create them

    self ! "test"

  }

  // Overriding postRestart to disable the call to preStart()
  // after restarts
  override def postRestart(reason: Throwable): Unit = ()

  // The default implementation of preRestart() stops all the children
  // of the actor. To opt-out from stopping the children, we
  // have to override preRestart()
  override def preRestart(reason: Throwable, message: Option[Any]): Unit = {
    // Keep the call to postStop(), but no stopping of children
    postStop()
  }

  def receive = {
    case "test" => Logger.info("asd1")
    case _ => Logger.info("asd")
  }
}

