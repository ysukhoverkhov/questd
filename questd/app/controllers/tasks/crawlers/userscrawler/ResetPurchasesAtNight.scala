package controllers.tasks.crawlers.userscrawler

import akka.actor.Props
import controllers.domain._
import controllers.domain.app.user._
import models.domain._
import java.util.Date
import components.random.RandomComponent

object ResetPurchasesAtNight {
  def props(api: DomainAPIComponent#DomainAPI, rand: RandomComponent#Random) = {
    Props(classOf[ResetPurchasesAtNight], api, rand)
  }

  def name = "ResetPurchasesAtNight"
}

class ResetPurchasesAtNight(
    apiPar: DomainAPIComponent#DomainAPI,
    randPar: RandomComponent#Random) extends BaseUserCrawler(apiPar, randPar)  {

  protected def check(user: User) = {
    if (user.schedules.purchases.before(new Date())) {
      api.resetPurchases(ResetPurchasesRequest(user))
    }
  }

}

