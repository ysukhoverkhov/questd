package controllers.tasks.crawlers.userscrawler

import akka.actor.Props
import controllers.domain._
import controllers.domain.app.user._
import models.domain._
import java.util.Date
import com.github.nscala_time.time.Imports._
import org.joda.time.DateTime
import components.random.RandomComponent

object CheckShiftDailyResult {
  def props(api: DomainAPIComponent#DomainAPI, rand: RandomComponent#Random) = {
    Props(classOf[CheckShiftDailyResult], api, rand)
  }

  def name = "CheckShiftDailyResult"
}

class CheckShiftDailyResult(
    apiPar: DomainAPIComponent#DomainAPI,
    randPar: RandomComponent#Random) extends BaseUserCrawler(apiPar, randPar)  {

  protected def check(user: User) = {
    if ((user.privateDailyResults.length == 0)
      || (new DateTime(user.privateDailyResults(0).startOfPeriod) + 1.day).toDate.before(new Date())) {
      api.shiftDailyResult(ShiftDailyResultRequest(user))
    }
  }

}

