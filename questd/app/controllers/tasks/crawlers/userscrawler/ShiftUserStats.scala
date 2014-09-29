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

object ShiftUserStats {
  def props(api: DomainAPIComponent#DomainAPI, rand: RandomComponent#Random) = {
    Props(classOf[ShiftUserStats], api, rand)
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

