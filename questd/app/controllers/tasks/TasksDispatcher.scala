package controllers.tasks

import akka.actor.Actor
import akka.actor.Props
import akka.event.Logging
import play.Logger

import us.theatr.akka.quartz._

object TasksDispatcher {
  def props = Props(classOf[TasksDispatcher], "TasksDispatcher")
}

class TasksDispatcher(name: String) extends Actor {

  override def preStart(): Unit = {

    val quartzActor = context.actorOf(Props[QuartzActor])
    quartzActor ! AddCronSchedule(self, "0/5 * * * * ?", "test")
    
    // TODO read configuration and create them
  }

  override def postRestart(reason: Throwable): Unit = ()

  override def preRestart(reason: Throwable, message: Option[Any]): Unit = {
    postStop()
  }

  
  def receive = {
    case "test" => Logger.info("asd1")
    case _ => Logger.info("asd")
  }
}

