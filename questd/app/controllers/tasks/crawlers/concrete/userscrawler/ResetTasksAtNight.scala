package controllers.tasks.crawlers.concrete.userscrawler

import java.util.Date

import akka.actor.Props
import components.random.RandomComponent
import controllers.domain._
import controllers.domain.app.user._
import controllers.tasks.crawlers.base.BaseCrawler
import models.domain.user.User

object ResetTasksAtNight {
  def props(api: DomainAPIComponent#DomainAPI, rand: RandomComponent#Random) = {
    Props(classOf[ResetTasksAtNight], api, rand)
  }

  def name = "ResetTasksAtNight"
}

class ResetTasksAtNight(
    apiPar: DomainAPIComponent#DomainAPI,
    randPar: RandomComponent#Random) extends BaseCrawler[User](apiPar, randPar)  {

  protected def check(user: User) = {
    if (user.schedules.nextDailyTasksAt.before(new Date())) {
      api.resetDailyTasks(ResetDailyTasksRequest(user))
    }
  }

}

