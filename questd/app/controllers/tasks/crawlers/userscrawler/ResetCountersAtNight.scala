package controllers.tasks.crawlers

import akka.actor.Actor
import akka.actor.Props
import play.Logger
import helpers.akka.EasyRestartActor
import controllers.tasks.messages.DoTask
import controllers.domain._
import controllers.domain.user._
import models.domain._
import java.util.Date

object ResetCountersAtNight {
  def props(api: DomainAPIComponent#DomainAPI) = {
    Props(classOf[ResetCountersAtNight], api)
  }
  
  def name = "ResetCountersAtNight"
}

class ResetCountersAtNight(api: DomainAPIComponent#DomainAPI) extends BaseUserCrawler(api) {

  protected def check(user: User) = {
    if (user.schedules.purchases.before(new Date())) {
      api.resetCounters(ResetCountersRequest(user))
    }
  }

}

