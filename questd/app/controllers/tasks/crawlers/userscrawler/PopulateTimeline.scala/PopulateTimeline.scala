package controllers.tasks.crawlers.userscrawler

import akka.actor.Props
import components.random.RandomComponent
import controllers.domain._
import controllers.domain.app.user._
import models.domain._

object PopulateTimeLine {
  def props(api: DomainAPIComponent#DomainAPI, rand: RandomComponent#Random) = {
    Props(classOf[PopulateTimeLine], api, rand)
  }

  def name = "ShiftUserStats"
}

class PopulateTimeLine(
    apiPar: DomainAPIComponent#DomainAPI,
    randPar: RandomComponent#Random) extends BaseUserCrawler(apiPar, randPar)  {

  protected def check(user: User) = {
    api.shiftStats(ShiftStatsRequest(user))
    api.shiftHistory(ShiftHistoryRequest(user))
  }

}

