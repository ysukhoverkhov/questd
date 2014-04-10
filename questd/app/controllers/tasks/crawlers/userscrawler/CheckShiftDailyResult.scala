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

import com.github.nscala_time.time.Imports._
import org.joda.time.DateTime

object CheckShiftDailyResult {
  def props(api: DomainAPIComponent#DomainAPI) = {
    Props(classOf[CheckShiftDailyResult], api)
  }

  def name = "CheckShiftDailyResult"
}

class CheckShiftDailyResult(api: DomainAPIComponent#DomainAPI) extends BaseUserCrawler(api) {

  protected def check(user: User) = {
    if (user.privateDailyResults.length == 0)
      api.shiftDailyResult(ShiftDailyResultRequest(user))
    else {
      if ((new DateTime(user.privateDailyResults(0).startOfPeriod) + 1.day).toDate.before(new Date()))
        api.shiftDailyResult(ShiftDailyResultRequest(user))
    }
  }

}

