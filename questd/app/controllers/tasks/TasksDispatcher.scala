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

  override def preStart(): Unit = {

    val quartzActor = context.actorOf(Props[QuartzActor])

    def schedule(path: String, cron: String) {

      actor(new Act {

        def identfySheduledWithRetry = {
          context.actorSelection(path) ! Identify("1")

          implicit val dispatcher = Akka.system.dispatcher
          Akka.system.scheduler.scheduleOnce(10 seconds, self, "OneMoreTimePlease")
        }

        whenStarting {
          identfySheduledWithRetry
        }

        become {
          case ActorIdentity("1", Some(a: ActorRef)) => {
            quartzActor ! AddCronSchedule(a, cron, DoTask)
            become {
              case "OneMoreTimePlease" => context.stop(self)
            }
          }

          case "OneMoreTimePlease" => identfySheduledWithRetry

          case _ => Logger.error("Error initializing actor - unable to start scheduler.")
        }
      })
    }

    schedule("akka://application/user/DummyCrawler", "0/5 * * * * ?")

    // TODO list all active tasks in admin page.

    // TODO IMPLEMENT read configuration and create them
  }

  def receive = {
    case "test" => Logger.info("asd1 -")
    case _ => Logger.info("asd")
  }
}

