package controllers.tasks.crawlers.concrete.userscrawler

import akka.actor.Props
import components.random.RandomComponent
import controllers.domain._
import controllers.domain.app.user._
import controllers.tasks.crawlers.base.BaseCrawler
import models.domain.user.User

object ShiftUserStats {
  def props(api: DomainAPIComponent#DomainAPI, rand: RandomComponent#Random) = {
    Props(classOf[PopulateTimeLine], api, rand)
  }

  def name = "ShiftUserStats"
}

class ShiftUserStats(
    apiPar: DomainAPIComponent#DomainAPI,
    randPar: RandomComponent#Random) extends BaseCrawler[User](apiPar, randPar)  {

  protected def check(user: User) = {
    api.shiftStats(ShiftStatsRequest(user))
  }

}

