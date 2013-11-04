package controllers.tasks

import scala.concurrent.duration._
import scala.language.postfixOps
import akka.actor._
import akka.actor.ActorDSL._
import akka.actor.ActorSystem
import us.theatr.akka.quartz._
import play.Logger
import play.api.libs.concurrent.Akka
import play.api.Play.current
import helpers.akka.EasyRestartActor
import controllers.tasks.messages.DoTask
import models.domain.config._

object TasksDispatcher {
  def props(config: ConfigSection) = Props(classOf[TasksDispatcher], config) 
  def name = "TasksDispatcher"
}

class TasksDispatcher(config: ConfigSection) extends EasyRestartActor {

  case class WakeCrawlerUp(crawler: ActorSelection)

  override def preStart(): Unit = {

    val quartzActor = context.actorOf(Props[QuartzActor])

    def schedule(path: String, cron: String) {
      quartzActor ! AddCronSchedule(self, cron, WakeCrawlerUp(context.actorSelection(path)))
    }
    
    config.values.foreach(c => schedule(c._1, c._2))
  }

  def receive = {

    case WakeCrawlerUp(c) => c ! DoTask

    case _ => Logger.error("Unexpected message received")
  }
}

