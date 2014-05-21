package controllers.tasks.crawlers.userscrawler

import akka.actor.Actor
import akka.actor.Props
import play.Logger
import helpers.akka.EasyRestartActor
import controllers.tasks.messages.DoTask
import controllers.domain._
import controllers.domain.app.user._
import models.domain._
import java.util.Date
import components.random.RandomComponent

object ResetTasksAtNight {
  def props(api: DomainAPIComponent#DomainAPI, rand: RandomComponent#Random) = {
    Props(classOf[ResetTasksAtNight], api, rand)
  }
  
  def name = "ResetTasksAtNight"
}

class ResetTasksAtNight(
    apiPar: DomainAPIComponent#DomainAPI,
    randPar: RandomComponent#Random) extends BaseUserCrawler(apiPar, randPar)  {

  protected def check(user: User) = {
    if (user.schedules.dailyTasks.before(new Date())) {
      api.resetDailyTasks(ResetDailyTasksRequest(user))
    }
  }

}

