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
import models.domain._
import models.domain.admin._

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

    printScheduledJobs
  }

  def printScheduledJobs = {
    // http://www.mkyong.com/java/how-to-list-all-jobs-in-the-quartz-scheduler/

   import org.quartz.impl.StdSchedulerFactory
    import org.quartz.impl.matchers.GroupMatcher
    import java.util.Date

    import scala.collection.JavaConversions._

    Thread.sleep(1000)

    val all = (new StdSchedulerFactory).getAllSchedulers()
    val scheduler = all.iterator().next()

    for (groupName <- scheduler.getJobGroupNames()) {
      for (jobKey <- scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {

        val jobName = jobKey.getName()
        val jobGroup = jobKey.getGroup()

        //get job's trigger
        val triggers = scheduler.getTriggersOfJob(jobKey);
        val nextFireTime = triggers.get(0).getNextFireTime();

        Logger.debug("[jobName] : " + jobName + " [groupName] : "
          + jobGroup + " - " + nextFireTime);

      }

    }
  }

  def receive = {

    case WakeCrawlerUp(c) => c ! DoTask

    case _ => Logger.error("Unexpected message received")
  }
}

