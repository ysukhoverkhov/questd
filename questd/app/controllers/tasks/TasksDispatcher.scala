package controllers.tasks

import akka.actor._
import com.vita.akka.EasyRestartActor
import controllers.tasks.messages.DoTask
import models.domain.admin._
import play.Logger
import us.theatr.akka.quartz._

import scala.language.postfixOps

object TasksDispatcher {
  def props(config: ConfigSection) = Props(classOf[TasksDispatcher], config)
  def name = "TasksDispatcher"
}

class TasksDispatcher(config: ConfigSection) extends Actor with EasyRestartActor {

  sealed trait TasksDispatcherEvents
  case class WakeCrawlerUp(crawler: ActorSelection) extends TasksDispatcherEvents

  override def preStart(): Unit = {
    Logger.debug("Starting scheduled tasks")

    val quartzActor = context.actorOf(Props[QuartzActor])

    def schedule(path: String, cron: String) {
      quartzActor ! AddCronSchedule(self, cron, WakeCrawlerUp(context.actorSelection(path)))
    }

    config.values.foreach{c =>
      Logger.debug(s"  Scheduling ${c._1} for ${c._2}")
      schedule(c._1, c._2)
    }

    printScheduledJobs()
  }

  def receive = {
    case WakeCrawlerUp(c) => c ! DoTask
    case _ => Logger.error("Unexpected message received")
  }



  private def printScheduledJobs() = {
    // http://www.mkyong.com/java/how-to-list-all-jobs-in-the-quartz-scheduler/

    import org.quartz.impl.StdSchedulerFactory
    import org.quartz.impl.matchers.GroupMatcher
    import scala.collection.JavaConversions._

    Thread.sleep(1000)

    Logger.debug("Scheduled tasks:")

    val all = (new StdSchedulerFactory).getAllSchedulers
    val scheduler = all.iterator().next()

    for (groupName <- scheduler.getJobGroupNames) {
      for (jobKey <- scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {

        val jobName = jobKey.getName
        val jobGroup = jobKey.getGroup

        //get job's trigger
        val triggers = scheduler.getTriggersOfJob(jobKey)
        val nextFireTime = triggers.get(0).getNextFireTime

        Logger.debug("  [jobName] : " + jobName + " [groupName] : "
          + jobGroup + " - " + nextFireTime)

      }
    }
  }
}

