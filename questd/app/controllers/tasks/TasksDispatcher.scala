package controllers.tasks

import akka.actor._
import controllers.tasks.messages.DoTask
import helpers.akka.EasyRestartActor
import models.domain.admin._
import play.Logger
import us.theatr.akka.quartz._

import scala.language.postfixOps

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

    printScheduledJobs()
  }

  def printScheduledJobs() = {
    // http://www.mkyong.com/java/how-to-list-all-jobs-in-the-quartz-scheduler/

    import org.quartz.impl.StdSchedulerFactory
    import org.quartz.impl.matchers.GroupMatcher

import scala.collection.JavaConversions._

    Thread.sleep(1000)

    val all = (new StdSchedulerFactory).getAllSchedulers
    val scheduler = all.iterator().next()

    for (groupName <- scheduler.getJobGroupNames) {
      for (jobKey <- scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {

        val jobName = jobKey.getName
        val jobGroup = jobKey.getGroup

        //get job's trigger
        val triggers = scheduler.getTriggersOfJob(jobKey)
        val nextFireTime = triggers.get(0).getNextFireTime

        Logger.debug("[jobName] : " + jobName + " [groupName] : "
          + jobGroup + " - " + nextFireTime)

      }

    }
  }

  def receive = {
    case WakeCrawlerUp(c) => c ! DoTask
    case _ => Logger.error("Unexpected message received")
  }
}

