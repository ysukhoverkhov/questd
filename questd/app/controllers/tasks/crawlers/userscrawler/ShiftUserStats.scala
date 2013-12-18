package controllers.tasks.crawlers

import akka.actor.Actor
import akka.actor.Props
import play.Logger
import helpers.akka.EasyRestartActor
import controllers.tasks.messages.DoTask
import controllers.domain._
import controllers.domain.app.user._
import models.domain._
import java.util.Date

object ShiftUserStats {
  def props(api: DomainAPIComponent#DomainAPI) = {
    Props(classOf[ShiftUserStats], api)
  }

  def name = "ShiftUserStats"
}

class ShiftUserStats(api: DomainAPIComponent#DomainAPI) extends BaseUserCrawler(api) {

  protected def check(user: User) = {
    api.shiftStats(ShiftStatsRequest(user))
  }

}

