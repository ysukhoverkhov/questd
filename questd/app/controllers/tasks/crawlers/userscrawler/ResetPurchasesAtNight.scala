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

object ResetPurchasesAtNight {
  def props(api: DomainAPIComponent#DomainAPI) = {
    Props(classOf[ResetPurchasesAtNight], api)
  }
  
  def name = "ResetPurchasesAtNight"
}

class ResetPurchasesAtNight(api: DomainAPIComponent#DomainAPI) extends BaseUserCrawler(api) {

  protected def check(user: User) = {
    if (user.schedules.purchases.before(new Date())) {
      api.resetPurchases(ResetPurchasesRequest(user))
    }
  }

}

