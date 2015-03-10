package controllers.tasks.crawlers.concrete.userscrawler

import akka.actor.Props
import com.github.nscala_time.time.Imports._
import components.random.RandomComponent
import controllers.domain._
import controllers.domain.app.user._
import controllers.tasks.crawlers.base.BaseCrawler
import models.domain._
import org.joda.time.DateTime

object ShiftDailyResult {
  def props(api: DomainAPIComponent#DomainAPI, rand: RandomComponent#Random) = {
    Props(classOf[ShiftDailyResult], api, rand)
  }

  def name = "ShiftDailyResult"
}

class ShiftDailyResult(
    apiPar: DomainAPIComponent#DomainAPI,
    randPar: RandomComponent#Random) extends BaseCrawler[User](apiPar, randPar)  {

  protected def check(user: User) = {
    if ((user.privateDailyResults.length == 0)
      || ((new DateTime(user.privateDailyResults.head.startOfPeriod) + 1.day) < DateTime.now())
        && user.isActive){
      api.shiftDailyResult(ShiftDailyResultRequest(user))
    }
  }

}

