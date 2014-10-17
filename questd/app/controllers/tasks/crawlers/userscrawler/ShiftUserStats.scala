package controllers.tasks.crawlers.userscrawler

import akka.actor.Props
import controllers.domain._
import controllers.domain.app.user._
import models.domain._
import components.random.RandomComponent

object ShiftUserStats {
  def props(api: DomainAPIComponent#DomainAPI, rand: RandomComponent#Random) = {
    Props(classOf[PopulateTimeLine], api, rand)
  }

  def name = "ShiftUserStats"
}

class ShiftUserStats(
    apiPar: DomainAPIComponent#DomainAPI,
    randPar: RandomComponent#Random) extends BaseUserCrawler(apiPar, randPar)  {

  protected def check(user: User) = {
    api.shiftStats(ShiftStatsRequest(user))
    api.shiftHistory(ShiftHistoryRequest(user))
  }

}

