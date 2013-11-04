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

object TasksDispatcher {
  def props = Props[TasksDispatcher]
  def name = "TasksDispatcher"
}

class TasksDispatcher extends EasyRestartActor {

  case class WakeCrawlerUp(crawler: ActorSelection)

  override def preStart(): Unit = {

    val quartzActor = context.actorOf(Props[QuartzActor])

    def schedule(path: String, cron: String) {
      quartzActor ! AddCronSchedule(self, cron, WakeCrawlerUp(context.actorSelection(path)))
    }

    
    schedule("akka://application/user/DummyCrawler", "0/5 * * * * ?")

    // TODO list all active tasks in admin page.

    // TODO IMPLEMENT read configuration and create them
  }

  def receive = {

    case WakeCrawlerUp(c) => c ! DoTask

    case _ => Logger.error("Unexpected message received")
  }
}

